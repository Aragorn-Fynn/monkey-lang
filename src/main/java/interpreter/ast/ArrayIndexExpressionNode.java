package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * array index expression
 * example: arr[1]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrayIndexExpressionNode implements ExpressionNode {
    private Token token;
    private ExpressionNode array;
    private ExpressionNode index;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer().append(array.toString());
        return res.append("([")
                .append(index.toString())
                .append("])").toString();
    }
}
