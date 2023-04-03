package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Map literal node
 * example: {1:2, 3:4}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapLiteralExpressionNode implements ExpressionNode {

    private Token token;
    private Map<ExpressionNode, ExpressionNode> pairs = new HashMap<>();

    public MapLiteralExpressionNode(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return pairs.toString();
    }
}
