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
public class IndexExpressionNode implements ExpressionNode {
    private Token token;
    private ExpressionNode object;
    private ExpressionNode index;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer().append(object.toString());
        return res.append("([")
                .append(index.toString())
                .append("])").toString();
    }
}
