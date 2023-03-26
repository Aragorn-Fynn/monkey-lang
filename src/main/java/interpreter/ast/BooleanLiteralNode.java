package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooleanLiteralNode implements ExpressionNode {
    private Token token;

    private Boolean value;

    @Override
    public String toString() {
        return token.getLiteral();
    }
}
