package interpreter.eval;

import interpreter.ast.*;
import interpreter.builtin.BuiltinFunctionEnum;
import interpreter.lexer.Token;
import interpreter.lexer.TokenTypeEnum;
import interpreter.macro.Macro;
import interpreter.object.*;
import lombok.Data;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * evaluate the program with tree traversal algorithm
 */
@Data
public class Evaluator {

    /**
     * return inner representation of monkey lang
     * @param node
     * @param env
     * @return
     */
    public ValueObject eval(TreeNode node, Environment env) {
        Class nodeClass = node.getClass();
        if (nodeClass.equals(ProgramNode.class)) {
            return evalProgram((ProgramNode) node, env);
        } else if (nodeClass.equals(LetStatementNode.class)) {
            return evalLetStatement((LetStatementNode) node, env);
        } else if (nodeClass.equals(ExpressionStatementNode.class)) {
            return eval(((ExpressionStatementNode) node).getExpression(), env);
        } else if (nodeClass.equals(ReturnStatementNode.class)) {
            return evalReturnStatement((ReturnStatementNode) node, env);
        } else if (nodeClass.equals(BlockStatement.class)) {
            return evalBlockStatement((BlockStatement) node, env);
        } else if (nodeClass.equals(IntegerLiteralNode.class)) {
            return new IntegerObject(((IntegerLiteralNode) node).getValue());
        } else if (nodeClass.equals(BooleanLiteralNode.class)) {
            return BooleanObject.getBooleanObject(((BooleanLiteralNode) node).getValue());
        } else if (nodeClass.equals(UnaryExpressionNode.class)) {
            return evalUnaryExpression((UnaryExpressionNode) node, env);
        } else if (nodeClass.equals(BinaryExpressionNode.class)) {
            return evalBinaryExpression((BinaryExpressionNode) node, env);
        } else if (nodeClass.equals(IfExpressionNode.class)) {
            return evalIfExpression((IfExpressionNode) node, env);
        } else if (nodeClass.equals(FunctionLiteralNode.class)) {
            return evalFunctionLiteral((FunctionLiteralNode) node, env);
        } else if (nodeClass.equals(CallExpressionNode.class)) {
            return evalCallExpression((CallExpressionNode) node, env);
        } else if (nodeClass.equals(IdentifierNode.class)) {
            return evalIdentifier((IdentifierNode) node, env);
        } else if (nodeClass.equals(StringLiteralNode.class)) {
            // eval string expression
            return new StringObject(((StringLiteralNode) node).getValue());
        } else if (nodeClass.equals(ArrayLiteralExpressionNode.class)) {
            return evalArrayLiteral((ArrayLiteralExpressionNode) node, env);
        } else if (nodeClass.equals(IndexExpressionNode.class)) {
            return evalArrayIndex((IndexExpressionNode) node, env);
        } else if (nodeClass.equals(MapLiteralExpressionNode.class)) {
            return evalMapLiteral((MapLiteralExpressionNode) node, env);
        } else if (nodeClass.equals(WhileExpressionNode.class)) {
            return evalWhileExpression((WhileExpressionNode) node, env);
        } else if (nodeClass.equals(FunctionStatementNode.class)) {
            return evalFunctionStatement((FunctionStatementNode) node, env);
        }

        return NullObject.getNullObject();
    }

    private ValueObject evalFunctionStatement(FunctionStatementNode node, Environment env) {
        FunctionObject function = new FunctionObject(node.getParameters(), node.getBody(), env);
        env.set(node.getFuncName().getValue(), function);
        return NullObject.getNullObject();
    }

    /**
     * eval while expression
     * 1. eval condition
     * 2. if condition is true, eval body
     * 3. if condition is false, break
     * @param node
     * @param env
     * @return
     */
    private ValueObject evalWhileExpression(WhileExpressionNode node, Environment env) {
        ValueObject res = NullObject.getNullObject();
        do {
            ValueObject cond = eval(node.getCondition(), env);
            if (cond.type() == ValueTypeEnum.ERROR) {
                return cond;
            }

            if (isTrue(cond)) {
                res = eval(node.getBody(), env);
            } else {
                break;
            }

        } while (true);
        return res;
    }

    /**
     * traverse the program, find macro call and expand it
     * 1. traverse the program
     * 2. if the node is macro call
     *    1. get the macro definition from env
     *    2. quote the arguments
     *    3. set args in the env
     *    4. eval macro body with env
     * 3. replace the macro call with the value returned from macro body
     * @param quoted
     * @param env
     * @return
     */
    public TreeNode expandMacro(TreeNode quoted, Environment env) {
        return Macro.modify(quoted, node -> {
            if (!node.getClass().equals(CallExpressionNode.class)) {
                return node;
            }

            if (!ifMacroCall((CallExpressionNode) node, env)) {
                return node;
            }

            CallExpressionNode call = (CallExpressionNode) node;
            // get the macro definition from env
            MacroObject macro = (MacroObject) env.get(call.getFuncName().toString());
            // quote the arguments
            List<QuoteObject> args = quoteArgs(call);

            // set args in the env
            Environment evalEnv = extendMacroEnv(macro, args);

            // bug-fix: support exprand macro twice
            MacroObject copied = SerializationUtils.clone(macro);
            // eval macro body with env
            ValueObject value = eval(copied.getBody(), evalEnv);

            if (!value.getClass().equals(QuoteObject.class)) {
                return null;
            }

            return ((QuoteObject) value).getNode();
        });
    }

    private Environment extendMacroEnv(MacroObject macro, List<QuoteObject> args) {
        Environment extended = new Environment(macro.getEnv());
        int i = 0;
        for (IdentifierNode para : macro.getParameters()) {
            extended.set(para.getValue().toString(), args.get(i));
            i++;
        }

        return extended;
    }

    private List<QuoteObject> quoteArgs(CallExpressionNode call) {
        List<QuoteObject> res = new ArrayList<>();
        for (ExpressionNode arg : call.getArguments()) {
            res.add(new QuoteObject(arg));
        }

        return res;
    }

    /**
     * check if the statement is macro call
     * @param node
     * @param env
     * @return
     */
    private boolean ifMacroCall(CallExpressionNode node, Environment env) {
        if (!node.getFuncName().getClass().equals(IdentifierNode.class)) {
            return false;
        }

        ValueObject value = env.get(node.getFuncName().toString());
        if (value == null) {
            return false;
        }

        if (!value.getClass().equals(MacroObject.class)) {
            return false;
        }

        return true;
    }

    /**
     * 1. find macro definition
     * 2. add macro definition to macroEnv
     * 3. delete macro definition from program
     * @param program
     * @param env
     */
    public void defineMacros(ProgramNode program, Environment env) {
        List<StatementNode> statements = new ArrayList<>();
        for (StatementNode statement : program.getStatements()) {
            if (isMacroDefinition(statement)) {
                addMacro(statement, env);
            } else {
                statements.add(statement);
            }
        }

        program.setStatements(statements);
    }

    /**
     * add macro definition to macroEnv
     * @param statement
     * @param env
     */
    private void addMacro(StatementNode statement, Environment env) {
        LetStatementNode letStatementNode = (LetStatementNode) statement;
        MacroLiteralNode macroLiteralNode = (MacroLiteralNode) letStatementNode.getValue();
        MacroObject macro = new MacroObject();
        macro.setEnv(env);
        macro.setParameters(macroLiteralNode.getParameters());
        // the body of macro
        macro.setBody(macroLiteralNode.getBody());
        env.set(letStatementNode.getName().getValue(), macro);
    }

    /**
     * check if statement is macro definition
     * @param statement
     * @return
     */
    private boolean isMacroDefinition(StatementNode statement) {
        if (!statement.getClass().equals(LetStatementNode.class)) {
            return false;
        }

        if (!((LetStatementNode) statement).getValue().getClass().equals(MacroLiteralNode.class)) {
            return false;
        }

        return true;
    }

    private ValueObject evalMapLiteral(MapLiteralExpressionNode node, Environment env) {
        MapObject res = new MapObject();

        Map<ExpressionNode, ExpressionNode> pairs = node.getPairs();
        for (Map.Entry<ExpressionNode, ExpressionNode> pair: pairs.entrySet()) {
            ValueObject key = eval(pair.getKey(), env);
            if (key.type() == ValueTypeEnum.ERROR) {
                return key;
            }

            ValueObject value = eval(pair.getValue(), env);
            if (value.type() == ValueTypeEnum.ERROR) {
                return value;
            }

            res.getPairs().put(key, value);
        }

        return res;
    }

    private ValueObject evalArrayIndex(IndexExpressionNode node, Environment env) {
        ValueObject object = eval(node.getObject(), env);
        if (object.type() == ValueTypeEnum.ERROR) {
            return object;
        }

        ValueObject index = eval(node.getIndex(), env);
        if (index.type() == ValueTypeEnum.ERROR) {
            return index;
        }

        if (object.type() == ValueTypeEnum.MAP) {
            MapObject mapObject = (MapObject) object;
            return mapObject.getPairs().get(index);
        } else if (object.type() == ValueTypeEnum.ARRAY && index.type() == ValueTypeEnum.INTEGER) {
            ArrayObject arrayObject = (ArrayObject) object;
            Integer idx =((IntegerObject) index).getValue();
            if (idx < 0 || idx > arrayObject.getElements().size() - 1) {
                return NullObject.getNullObject();
            }

            return arrayObject.getElements().get(idx);
        } else {
            return new ErrorObject(String.format("index not supported: %s", object.type()));
        }
    }

    private ValueObject evalArrayLiteral(ArrayLiteralExpressionNode node, Environment env) {
        List<ExpressionNode> elements = node.getElements();
        List<ValueObject> ele = evalExpressions(elements, env);
        if (ele.size() == 1 && ele.get(0).type() == ValueTypeEnum.ERROR) {
            return ele.get(0);
        }

        ArrayObject array = new ArrayObject(ele);
        return array;
    }

    /**
     * get the real value of identifier from the context
     * @param node
     * @param env
     * @return
     */
    private ValueObject evalIdentifier(IdentifierNode node, Environment env) {
        ValueObject value = env.get(node.getValue());
        if (value != null) {
            return value;
        }

        value = BuiltinFunctionEnum.getBuiltinFunctionOf(node.getValue());

        if (value == null) {
            return new ErrorObject(String.format("identifier not found: %s", node.getValue()));
        }

        return value;
    }

    /**
     * return the function with current env, implement closure
     * @param node
     * @param env
     * @return
     */
    private ValueObject evalFunctionLiteral(FunctionLiteralNode node, Environment env) {
        return new FunctionObject(node.getParameters(), node.getBody(), env);
    }

    /**
     * eval call expression:
     * 1. get function from context
     * 2. eval arguments expression
     * 3. apply function with the args
     * @param node
     * @param env
     * @return
     */
    private ValueObject evalCallExpression(CallExpressionNode node, Environment env) {

        if (node.getFuncName().toString().equals("quote")) {
            return evalQuote(node.getArguments().get(0), env);
        }

        ValueObject function = eval(node.getFuncName(), env);
        if (function.type() == ValueTypeEnum.ERROR) {
            return function;
        }

        List<ValueObject> args = evalExpressions(node.getArguments(), env);
        if (args.size() == 1 && args.get(0).type() == ValueTypeEnum.ERROR) {
            return args.get(0);
        }

        return applyFunction(function, args);
    }

    /**
     * eval quote node
     * @param quoted
     * @param env
     * @return
     */
    private ValueObject evalQuote(TreeNode quoted, Environment env) {
        /**
         * traverse the tree node, if the node is unquote call, eval the value of the node
         * @param node
         * @return
         */
        quoted = Macro.modify(quoted, node -> {
            // if the node is not call expression, don't modify, return directly
            if (!node.getClass().equals(CallExpressionNode.class)) {
                return node;
            }

            // if the call is not unquote call, don't modify, return directly
            if (!ifUnquoteCall(node)) {
                return node;
            }

            CallExpressionNode call = (CallExpressionNode) node;
            // if the call has more than one argument, don't modify, return directly, because unquote only has one argument
            if (call.getArguments().size() != 1) {
                return node;
            }

            // eval value of unquoted tree node
            ValueObject value = eval(((CallExpressionNode) node).getArguments().get(0), env);
            // replce the unquote call with the value of the unquoted tree node
            return convertObjectToAstNode(value);
        });
        return new QuoteObject(quoted);
    }

    /**
     * convert the object to ast node
     * @param value
     * @return
     */
    private TreeNode convertObjectToAstNode(ValueObject value) {
        switch (value.type()) {
            case INTEGER:
                return new IntegerLiteralNode(new Token(TokenTypeEnum.INT, value.inspect()), ((IntegerObject) value).getValue());
            case BOOLEAN:
                BooleanObject v = (BooleanObject) value;
                return new BooleanLiteralNode(new Token(v.getValue()?TokenTypeEnum.TRUE:TokenTypeEnum.FALSE, v.inspect()), v.getValue());
            case QUOTE:
                return ((QuoteObject) value).getNode();
            default:
                return null;
        }
    }

    /**
     * check if the node is unquote call
     * @param node
     * @return
     */
    private boolean ifUnquoteCall(TreeNode node) {
        if (!node.getClass().equals(CallExpressionNode.class))
            return false;

        return "unquote".equals(((CallExpressionNode) node).getFuncName().toString());
    }

    /**
     * 1. create context of the function
     * 2. set args in the context
     * 3. eval the statements in function body
     * 4. return value;
     * @param function
     * @param args
     * @return
     */
    private ValueObject applyFunction(ValueObject function, List<ValueObject> args) {
        ValueObject res = null;

        switch (function.type()) {
            case FUNCTION:
                FunctionObject fn = (FunctionObject) function;
                Environment extendEnv = new Environment(fn.getEnv());
                int i = 0;
                for (IdentifierNode para : fn.getParameters()) {
                    extendEnv.set(para.getValue().toString(), args.get(i));
                    i++;
                }
                res = eval(((FunctionObject) function).getBody(), extendEnv);

                if (res != null && res.type() == ValueTypeEnum.RETURN) {
                    res = ((ReturnObject)res).getValue();
                }
                break;
            case BUILTIN:
                BuiltinFunctionObject builtinFunc = (BuiltinFunctionObject) function;
                res = builtinFunc.getFunction().apply(args);
                break;
            default:
                return new ErrorObject(String.format("not a function: %s", function.type()));
        }




        return res;
    }

    private List<ValueObject> evalExpressions(List<ExpressionNode> arguments, Environment env) {
        List<ValueObject> res = new ArrayList<>();
        for (ExpressionNode arg : arguments) {
            ValueObject r = eval(arg, env);
            if (r.type() == ValueTypeEnum.ERROR) {
                return Arrays.asList(r);
            }

            res.add(r);
        }

        return res;
    }

    private ValueObject evalReturnStatement(ReturnStatementNode node, Environment env) {
        ValueObject value = eval(node.getValue(), env);
        if (value.type() == ValueTypeEnum.ERROR) {
            return value;
        }
        return new ReturnObject(value);
    }

    private ValueObject evalLetStatement(LetStatementNode node, Environment env) {
        ValueObject value = eval(node.getValue(), env);
        if (value.type() == ValueTypeEnum.ERROR) {
            return value;
        }

        env.set(node.getName().getValue(), value);
        return NullObject.getNullObject();
    }

    private ValueObject evalBlockStatement(BlockStatement node, Environment env) {
        ValueObject res = null;
        for (StatementNode statement : node.getStatements()) {
            res = eval(statement, env);
            if (res != null && (res.type() == ValueTypeEnum.ERROR || res.type() == ValueTypeEnum.RETURN)) {
                return res;
            }
        }

        return res;
    }

    private ValueObject evalIfExpression(IfExpressionNode node, Environment env) {
        ValueObject condition = eval(node.getCondition(), env);

        if (condition.type() == ValueTypeEnum.ERROR) {
            return condition;
        }

        if (isTrue(condition)) {
            return eval(node.getConsequence(), env);
        } else if (node.getAlternative() != null) {
            return eval(node.getAlternative(), env);
        } else {
            return NullObject.getNullObject();
        }
    }

    private boolean isTrue(ValueObject condition) {
        switch (condition.type()) {
            case NULL:
                return false;
            case BOOLEAN:
                return ((BooleanObject) condition).getValue();
            default:
                return true;
        }
    }

    private ValueObject evalBinaryExpression(BinaryExpressionNode node, Environment env) {
        ValueObject left = eval(node.getLeft(), env);

        if (left.type() == ValueTypeEnum.ERROR) {
            return left;
        }

        ValueObject right = eval(node.getRight(), env);

        if (right.type() == ValueTypeEnum.ERROR) {
            return right;
        }

        if (left.type() != right.type()) {
            return new ErrorObject(String.format("type missmatch: %s %s %s", left.type(), node.getOperator(), right.type()));
        } else if (left.type() == ValueTypeEnum.INTEGER && right.type() == ValueTypeEnum.INTEGER) {
            IntegerObject leftInt = (IntegerObject) left;
            IntegerObject rightInt = (IntegerObject) right;
            switch (node.getOperator()) {
                case "+":
                    return new IntegerObject(leftInt.getValue() + rightInt.getValue());
                case "-":
                    return new IntegerObject(leftInt.getValue() - rightInt.getValue());
                case "*":
                    return new IntegerObject(leftInt.getValue() * rightInt.getValue());
                case "/":
                    return new IntegerObject(leftInt.getValue() / rightInt.getValue());
                case "<":
                    return BooleanObject.getBooleanObject(leftInt.getValue() < rightInt.getValue());
                case ">":
                    return BooleanObject.getBooleanObject(leftInt.getValue() > rightInt.getValue());
                case "!=":
                    return BooleanObject.getBooleanObject(leftInt.getValue().intValue() != rightInt.getValue().intValue());
                case "==":
                    return BooleanObject.getBooleanObject(leftInt.getValue().intValue() == rightInt.getValue().intValue());
                default:
                    return new ErrorObject(String.format("unknown operator: %s %s %s", left.type(), node.getOperator(), right.type()));
            }
        } else if (left.type() == ValueTypeEnum.STRING && "+".equals(node.getOperator())) {
            return new StringObject(((StringObject)left).getValue() + ((StringObject)right).getValue());
        } else if ("==".equals(node.getOperator())) {
            return BooleanObject.getBooleanObject(left == right);
        } else if ("!=".equals(node.getOperator())) {
            return BooleanObject.getBooleanObject(left != right);
        }
        return new ErrorObject(String.format("unknown operator: %s %s %s", left.type(), node.getOperator(), right.type()));
    }

    private ValueObject evalUnaryExpression(UnaryExpressionNode node, Environment env) {
        ValueObject right = eval(node.getRight(), env);

        if (right.type() == ValueTypeEnum.ERROR) {
            return right;
        }

        switch (node.getOperator()) {
            case "!":
                switch (right.type()) {
                    case BOOLEAN:
                        return BooleanObject.getBooleanObject(!((BooleanObject)right).getValue());
                    case INTEGER:
                        return BooleanObject.getBooleanObject(((IntegerObject)right).getValue() == 0);
                    default:
                        return BooleanObject.getBooleanObject(true);
                }
            case "-":
                if (right.type() != ValueTypeEnum.INTEGER) {
                    return new ErrorObject(String.format("unknown operator: %s%s", node.getOperator(), right.type().name()));
                }

                return new IntegerObject(-1* ((IntegerObject) right).getValue());
            default:
                return new ErrorObject(String.format("unknown operator: %s%s", node.getOperator(), right.type().name()));
        }
    }

    private ValueObject evalProgram(ProgramNode node, Environment env) {
        ValueObject res = null;
        for (StatementNode statement : node.getStatements()) {
            res = eval(statement, env);
            if (res == null) {
                continue;
            }
            if (res.type() == ValueTypeEnum.RETURN) {
                return ((ReturnObject) res).getValue();
            } else if (res.type() == ValueTypeEnum.ERROR) {
                return res;
            }
        }

        return res;
    }
}
