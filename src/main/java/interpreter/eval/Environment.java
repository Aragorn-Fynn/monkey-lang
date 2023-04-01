package interpreter.eval;

import interpreter.object.ValueObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * context while interpreting the statement, store the value of identifier
 */
@Data
@NoArgsConstructor

public class Environment {
    /**
     * the map should not be static, if it is static, the outer context will share the same map, it is wrong.
     */
    private final Map<String, ValueObject> env = new HashMap<>();
    private Environment outer;

    public Environment(Environment outer) {
        this.outer = outer;
    }

    public void set(String name, ValueObject value) {
        env.put(name, value);
    }

    public ValueObject get(String name) {
        if (env.containsKey(name)) {
            return env.get(name);
        }

        if (outer != null) {
            return outer.get(name);
        }

        return null;
    }
}
