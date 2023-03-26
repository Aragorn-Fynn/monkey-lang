package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 一元运算符
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnaryExpressionNode implements ExpressionNode {
    private Token token;
    private String operator;
    private ExpressionNode right;

    @Override
    public String toString() {
        return "(" + this.operator + right.toString() + ")";
    }
}
