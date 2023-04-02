package interpreter.ast;

import interpreter.lexer.Token;
import lombok.Data;

/**
 * expression statement;
 * example: 1+2;
 */
@Data
public class ExpressionStatementNode implements StatementNode {
    private Token token;
    private ExpressionNode expression;

    public String toString() {
        return (expression == null ? "" : expression.toString()+ ";");
    }
}
