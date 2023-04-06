package interpreter.object;

import java.io.Serializable;

/**
 * inner representation of value in monkey language
 */
public interface ValueObject extends Serializable {
    ValueTypeEnum type();
    String inspect();
}
