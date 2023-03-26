package interpreter.ast;

import interpreter.lexer.Token;
import lombok.Data;

/**
 * class of return statement
 */
@Data
public class ReturnStatementNode implements StatementNode {

    /**
     * return token
     */
    private Token token;

    /**
     * expression node
     */
    private ExpressionNode expression;

    @Override
    public String toString() {
        return token.getLiteral() + " " + (expression != null ? expression.toString() : "") + ";";
    }
}
