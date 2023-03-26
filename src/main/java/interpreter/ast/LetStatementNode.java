package interpreter.ast;

import interpreter.lexer.Token;
import lombok.Data;

/**
 * node class of let statemnet
 */
@Data
public class LetStatementNode implements StatementNode {
    Token token;

    private IdentifierNode identifier;

    private ExpressionNode expression;

    @Override
    public String toString() {
        return token.getLiteral() + " "
                + (identifier == null ? "" : identifier.toString()) + " = "
                + (expression == null ? "" : expression.toString())+";";
    }
}
