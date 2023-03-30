package interpreter.eval;

import interpreter.ast.*;
import interpreter.object.*;
import lombok.Data;

/**
 * 树求值器
 */
@Data
public class Evaluator {

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
            return evalFunctionLiteral((FunctionLiteralNode) node);
        } else if (nodeClass.equals(CallExpressionNode.class)) {
            return evalCallExpression((CallExpressionNode) node);
        } else if (nodeClass.equals(IdentifierNode.class)) {
            return evalIdentifier((IdentifierNode) node, env);
        }

        return NullObject.getNullObject();
    }

    private ValueObject evalIdentifier(IdentifierNode node, Environment env) {
        ValueObject value = env.get(node.getValue());
        if (value == null) {
            return new ErrorObject(String.format("identifier not found: %s", value));
        }

        return value;
    }

    private ValueObject evalFunctionLiteral(FunctionLiteralNode node) {
        return null;
    }

    private ValueObject evalCallExpression(CallExpressionNode node) {
        return null;
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
        return value;
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
            if (res.type() == ValueTypeEnum.RETURN) {
                return ((ReturnObject) res).getValue();
            } else if (res.type() == ValueTypeEnum.ERROR) {
                return res;
            }
        }

        return res;
    }
}
