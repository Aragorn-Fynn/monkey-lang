package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallExpressionNode implements ExpressionNode {
    private Token token;

    private ExpressionNode funcName;

    private List<ExpressionNode> arguments;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(funcName.toString())
                .append("(")
                .append(arguments.stream().map(item -> item.toString()).collect(Collectors.joining(", ")))
                .append(")");

        return res.toString();
    }
}
