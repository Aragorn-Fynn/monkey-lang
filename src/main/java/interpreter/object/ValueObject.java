package interpreter.object;

/**
 * 对象系统接口
 */
public interface ValueObject {
    ValueTypeEnum type();
    String inspect();
}
