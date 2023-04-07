package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * statement tree node
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionStatementNode implements StatementNode {
    private Token token;
    private IdentifierNode funcName;
    private List<IdentifierNode> parameters = new ArrayList<>();
    private BlockStatement body;

    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("fn ").append(funcName.toString());
        res.append("(");
        res.append(parameters.stream().map(item -> item.toString()).collect(Collectors.joining(", ")));
        res.append(") ");
        res.append(body.toString());
        return res.toString();
    }
}
