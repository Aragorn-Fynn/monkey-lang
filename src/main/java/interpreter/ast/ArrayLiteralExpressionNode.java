package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * array literal
 * example: [1,2,3]
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrayLiteralExpressionNode implements ExpressionNode {
    private Token token;
    private List<ExpressionNode> elements;

    public ArrayLiteralExpressionNode(Token token) {
        this.token = token;
        this.elements = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer("([");
        return res.append(elements.stream()
                .map(item -> item.toString())
                .collect(Collectors.joining(",")))
                .append("])")
                .toString();
    }
}
