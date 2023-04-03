package interpreter.object;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class MapObject implements ValueObject {

    private ValueTypeEnum type;
    private Map<ValueObject, ValueObject> pairs = new HashMap<>();

    public MapObject() {
        this.type = ValueTypeEnum.MAP;
    }

    @Override
    public ValueTypeEnum type() {
        return this.type;
    }

    @Override
    public String inspect() {
        return pairs.toString();
    }
}
