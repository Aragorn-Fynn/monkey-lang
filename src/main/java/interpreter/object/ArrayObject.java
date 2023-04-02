package interpreter.object;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ArrayObject implements ValueObject {

    private ValueTypeEnum type;

    private List<ValueObject> elements = new ArrayList<>();

    public ArrayObject(List<ValueObject> elements) {
        this.elements = elements;
        this.type = ValueTypeEnum.ARRAY;
    }

    @Override
    public ValueTypeEnum type() {
        return this.type;
    }

    @Override
    public String inspect() {
        StringBuffer res = new StringBuffer();
        return res.append("[")
                .append(elements.stream()
                        .map(item -> item.inspect())
                        .collect(Collectors.joining(",")))
                .append("]")
                .toString();
    }
}
