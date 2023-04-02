package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Binary Expression
 * example: 1+2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinaryExpressionNode implements ExpressionNode {
    private Token token;
    private ExpressionNode left;
    private String operator;

    private ExpressionNode right;

    @Override
    public String toString() {
        return "(" + left.toString() + " " + operator + " " + right.toString() + ")";
    }
}
