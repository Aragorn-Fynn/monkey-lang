package interpreter.object;

import lombok.Data;

/**
 * 空对象
 */
@Data
public class NullObject implements ValueObject {

    private ValueTypeEnum type;

    public NullObject() {
        this.type = ValueTypeEnum.NULL_OBJ;
    }

    @Override
    public ValueTypeEnum type() {
        return this.type;
    }

    @Override
    public String inspect() {
        return "NULL";
    }

    private static final NullObject NULL_OBJECT = new NullObject();

    public static NullObject getNullObject() {
        return NULL_OBJECT;
    }
}
