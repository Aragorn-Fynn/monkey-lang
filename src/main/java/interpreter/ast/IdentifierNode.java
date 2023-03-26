package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * node class of identifier
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentifierNode implements ExpressionNode {
    private Token token;

    private String value;

    @Override
    public String toString() {
        return value;
    }
}
