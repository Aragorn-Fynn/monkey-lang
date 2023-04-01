package interpreter.object;

import lombok.Data;

@Data
public class StringObject implements ValueObject {

    private ValueTypeEnum type;
    private String value;

    public StringObject(String value) {
        this.value = value;
        this.type = ValueTypeEnum.STRING;
    }

    @Override
    public ValueTypeEnum type() {
        return this.type;
    }

    @Override
    public String inspect() {
        return value;
    }
}
