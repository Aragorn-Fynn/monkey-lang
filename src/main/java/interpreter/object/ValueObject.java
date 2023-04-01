package interpreter.object;

/**
 * inner representation of value in monkey language
 */
public interface ValueObject {
    ValueTypeEnum type();
    String inspect();
}
