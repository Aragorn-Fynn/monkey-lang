package interpreter.ast;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * the root of ast
 */
@Data
public class ProgramNode implements TreeNode {
    /**
     * a list of statement
     */
    private List<StatementNode> statements;

    public ProgramNode() {
        this.statements = new ArrayList<>();
    }

    @Override
    public String literal() {
        return statements.stream()
                .map(item -> item.literal())
                .collect(Collectors.joining("\n"));
    }
}
