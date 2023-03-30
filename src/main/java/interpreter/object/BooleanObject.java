package interpreter.object;

import lombok.Data;

/**
 * 布尔值对象
 */
@Data
public class BooleanObject implements ValueObject {

    private final ValueTypeEnum type;
    private Boolean value;

    public BooleanObject(Boolean value) {
        this.value = value;
        this.type = ValueTypeEnum.BOOLEAN_OBJ;
    }

    @Override
    public ValueTypeEnum type() {
        return type;
    }

    @Override
    public String inspect() {
        return value.toString();
    }

    private static final BooleanObject TRUE = new BooleanObject(true);
    private static final BooleanObject FALSE = new BooleanObject(false);

    public static BooleanObject getBooleanObject(Boolean value) {
        return value ? TRUE : FALSE;
    }
}
