package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockStatement implements StatementNode {
    private Token token;

    private List<StatementNode> statements = new ArrayList<>();

    @Override
    public String toString() {
        return statements.stream()
                .map(item -> item.toString())
                .collect(Collectors.joining("\n"));
    }
}
