package interpreter.object;

import interpreter.ast.BlockStatement;
import interpreter.ast.IdentifierNode;
import interpreter.eval.Environment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * macro object is like function object, but it's different from function object.
 * macro object is composed with quote node, when it's called, it will expand the quote node, and then replace the node with the expanded node.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MacroObject implements ValueObject {

    private List<IdentifierNode> parameters = new ArrayList<>();

    private BlockStatement body;

    private Environment env;

    @Override
    public ValueTypeEnum type() {
        return ValueTypeEnum.MACRO;
    }

    @Override
    public String inspect() {
        StringBuffer res = new StringBuffer();
        return res.append("macro(")
                .append(parameters.stream().map(item -> item.toString()).collect(Collectors.joining(",")))
                .append(") {\n")
                .append(body.toString())
                .append("\n}").toString();
    }
}
