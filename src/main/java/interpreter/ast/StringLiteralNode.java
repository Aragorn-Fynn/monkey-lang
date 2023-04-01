package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * string node in ast
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringLiteralNode implements ExpressionNode {

    private Token token;
    private String value;

    public String toString() {
        return value;
    }
}
