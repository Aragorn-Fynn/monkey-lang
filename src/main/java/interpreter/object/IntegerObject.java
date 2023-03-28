package interpreter.object;

import lombok.Data;

/**
 * 整数值对象
 */
@Data
public class IntegerObject implements ValueObject {

    private ValueTypeEnum type;
    private Integer value;

    public IntegerObject(Integer value) {
        this.value = value;
        this.type = ValueTypeEnum.INTEGER_OBJ;
    }

    @Override
    public ValueTypeEnum type() {
        return this.type;
    }

    @Override
    public String inspect() {
        return value.toString();
    }
}
