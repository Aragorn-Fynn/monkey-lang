package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * example:{1+2; 3+4;}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockStatement implements StatementNode {
    private Token token;

    private List<StatementNode> statements = new ArrayList<>();

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("{\n");
        for (StatementNode statement : statements) {
            res.append(statement.toString()).append("\n");
        }
        res.append("}");
        return res.toString();
    }
}
