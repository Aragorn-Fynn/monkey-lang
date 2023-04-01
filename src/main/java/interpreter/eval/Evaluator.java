package interpreter.eval;

import interpreter.ast.*;
import interpreter.builtin.BuiltinFunctionEnum;
import interpreter.object.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        }

        return NullObject.getNullObject();
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
                if (res.type() == ValueTypeEnum.RETURN) {
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
        return null;
    }

    private ValueObject evalBlockStatement(BlockStatement node, Environment env) {
        ValueObject res = null;
        for (StatementNode statement : node.getStatements()) {
            res = eval(statement, env);
            if (res.type() == ValueTypeEnum.ERROR || res.type() == ValueTypeEnum.RETURN) {
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
