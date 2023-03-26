package interpreter.ast;

import interpreter.lexer.Token;
import lombok.Data;

/**
 * node class of let statemnet
 */
@Data
public class LetStatementNode implements StatementNode {
    Token token;

    private IdentifierNode name;

    private ExpressionNode value;

    @Override
    public String toString() {
        return token.getLiteral() + " "
                + (name == null ? "" : name.toString()) + " = "
                + (value == null ? "" : value.toString())+";";
    }
}
