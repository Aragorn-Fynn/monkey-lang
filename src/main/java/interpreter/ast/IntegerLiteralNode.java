package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * class of integer literal node
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegerLiteralNode implements ExpressionNode {
    private Token token;
    private Integer value;

    @Override
    public String toString() {
        return token.getLiteral();
    }
}
