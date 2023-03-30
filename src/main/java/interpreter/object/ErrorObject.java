package interpreter.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorObject implements ValueObject {

    String message;

    @Override
    public ValueTypeEnum type() {
        return ValueTypeEnum.ERROR;
    }

    @Override
    public String inspect() {
        return String.format("ERROR: %s", message);
    }
}
