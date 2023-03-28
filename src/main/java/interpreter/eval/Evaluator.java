package interpreter.eval;

import interpreter.ast.*;
import interpreter.object.BooleanObject;
import interpreter.object.IntegerObject;
import interpreter.object.NullObject;
import interpreter.object.ValueObject;
import lombok.Data;

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
            return new BooleanObject(((BooleanLiteralNode) node).getValue());
        }

        return new NullObject();
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
