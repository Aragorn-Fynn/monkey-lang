package interpreter.object;

import lombok.Data;

import java.util.List;
import java.util.function.Function;

@Data
public class BuiltinFunctionObject implements ValueObject {

    private ValueTypeEnum type;
    private Function<List<ValueObject>, ValueObject> function;

    public BuiltinFunctionObject(Function function) {
        this.function = function;
        this.type = ValueTypeEnum.BUILTIN;
    }

    @Override
    public ValueTypeEnum type() {
        return this.type;
    }

    @Override
    public String inspect() {
        return "BUILTIN FUNCTION";
    }
}
