package interpreter.eval;

import interpreter.object.ValueObject;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * bind the identifier to value
 */
@Data
@NoArgsConstructor

public class Environment {
    private static final Map<String, ValueObject> env = new HashMap<>();
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
