package interpreter.eval;

import interpreter.ast.*;
import interpreter.object.*;
import lombok.Data;

/**
 * 树求值器
 */
@Data
public class Evaluator {

    public ValueObject eval(TreeNode node) {
        Class nodeClass = node.getClass();
        if (nodeClass.equals(ProgramNode.class)) {
            return evalProgram((ProgramNode) node);
        } else if (nodeClass.equals(ExpressionStatementNode.class)) {
            return eval(((ExpressionStatementNode) node).getExpression());
        } else if (nodeClass.equals(IntegerLiteralNode.class)) {
            return new IntegerObject(((IntegerLiteralNode) node).getValue());
        } else if (nodeClass.equals(BooleanLiteralNode.class)) {
            return BooleanObject.getBooleanObject(((BooleanLiteralNode) node).getValue());
        } else if (nodeClass.equals(UnaryExpressionNode.class)) {
            return evalUnaryExpression((UnaryExpressionNode) node);
        } else if (nodeClass.equals(BinaryExpressionNode.class)) {
            return evalBinaryExpression((BinaryExpressionNode) node);
        } else if (nodeClass.equals(IfExpressionNode.class)) {
            return evalIfExpression((IfExpressionNode) node);
        } else if (nodeClass.equals(BlockStatement.class)) {
            return evalBlockStatement((BlockStatement) node);
        } else if (nodeClass.equals(LetStatementNode.class)) {
            return evalLetStatement((LetStatementNode) node);
        } else if (nodeClass.equals(FunctionLiteralNode.class)) {
            return evalFunctionLiteral((FunctionLiteralNode) node);
        } else if (nodeClass.equals(CallExpressionNode.class)) {
            return evalCallExpression((CallExpressionNode) node);
        } else if (nodeClass.equals(ReturnStatementNode.class)) {
            return evalReturnStatement((ReturnStatementNode) node);
        }

        return NullObject.getNullObject();
    }

    private ValueObject evalFunctionLiteral(FunctionLiteralNode node) {
        return null;
    }

    private ValueObject evalCallExpression(CallExpressionNode node) {
        return null;
    }

    private ValueObject evalReturnStatement(ReturnStatementNode node) {
        return null;
    }

    private ValueObject evalLetStatement(LetStatementNode node) {
        return null;
    }

    private ValueObject evalBlockStatement(BlockStatement node) {
        ValueObject res = null;
        for (StatementNode statement :
                node.getStatements()) {
            res = eval(statement);
        }

        return res;
    }

    private ValueObject evalIfExpression(IfExpressionNode node) {
        return null;
    }

    private ValueObject evalBinaryExpression(BinaryExpressionNode node) {
        return null;
    }

    private ValueObject evalUnaryExpression(UnaryExpressionNode node) {
        ValueObject res = eval(node.getRight());
        switch (res.type()) {
            case INTEGER_OBJ:
                IntegerObject intValue = (IntegerObject) res;
                switch (node.getOperator()) {
                    case "-":
                        return new IntegerObject(-intValue.getValue());
                    case "!":
                        return BooleanObject.getBooleanObject(intValue.getValue() == 0);
                    default:
                        return NullObject.getNullObject();
                }
            case BOOLEAN_OBJ:
                BooleanObject boolObj = (BooleanObject) res;
                switch (node.getOperator()) {
                    case "!":
                        return BooleanObject.getBooleanObject(!boolObj.getValue());
                    default:
                        return NullObject.getNullObject();
                }
            default:
                return NullObject.getNullObject();
        }
    }

    private ValueObject evalProgram(ProgramNode node) {
        ValueObject res = null;
        for (StatementNode statement :
                node.getStatements()) {
            res = eval(statement);
        }

        return res;
    }
}
