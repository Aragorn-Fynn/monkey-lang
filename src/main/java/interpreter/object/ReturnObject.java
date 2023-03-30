package interpreter.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * return value object
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnObject implements ValueObject {

    private ValueObject value;

    @Override
    public ValueTypeEnum type() {
        return ValueTypeEnum.RETURN;
    }

    @Override
    public String inspect() {
        return value.inspect();
    }
}
