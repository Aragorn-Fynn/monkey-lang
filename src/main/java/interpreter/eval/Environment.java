package interpreter.eval;

import interpreter.object.ValueObject;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * bind the identifier to value
 */
@Data
public class Environment {
    private static final Map<String, ValueObject> env = new HashMap<>();

    public void set(String name, ValueObject value) {
        env.put(name, value);
    }

    public ValueObject get(String name) {
        return env.get(name);
    }
}
