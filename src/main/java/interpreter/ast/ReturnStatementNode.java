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
    private ExpressionNode value;

    @Override
    public String toString() {
        return token.getLiteral() + " " + (value != null ? value.toString() : "") + ";";
    }
}
