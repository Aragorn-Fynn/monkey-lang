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
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionObject implements ValueObject {

    private List<IdentifierNode> parameters = new ArrayList<>();

    private BlockStatement body;

    private Environment env;

    @Override
    public ValueTypeEnum type() {
        return ValueTypeEnum.FUNCTION;
    }

    @Override
    public String inspect() {
        StringBuffer res = new StringBuffer();
        return res.append("fn(")
                .append(parameters.stream().map(item -> item.toString()).collect(Collectors.joining(",")))
                .append(") {\n")
                .append(body.toString())
                .append("\n}").toString();
    }
}
