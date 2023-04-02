package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * example: fn(x, y) {return x+y;}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionLiteralNode implements ExpressionNode {
    private Token token;

    private List<IdentifierNode> parameters = new ArrayList<>();

    private BlockStatement body;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(token.getLiteral())
                .append("(").append(parameters.stream().map(item ->item.toString()).collect(Collectors.joining(", "))).append(") ")
                .append(body.toString());

        return res.toString();
    }
}
