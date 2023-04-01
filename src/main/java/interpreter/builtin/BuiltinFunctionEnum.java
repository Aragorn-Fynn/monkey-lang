package interpreter.builtin;

import interpreter.object.*;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Getter
public enum BuiltinFunctionEnum {

    LEN("len", lenFunction());

    private static Function<List<ValueObject>, ValueObject> lenFunction() {
        return args -> {
            if (args == null || args.size() != 1) {
                return new ErrorObject(String.format("wrong number of arguments, got=%d, want=1", args.size()));
            }

            ValueObject arg = args.get(0);
            switch (arg.type()) {
                case STRING:
                    return new IntegerObject(((StringObject) arg).getValue().length());
                default:
                    return new ErrorObject(String.format("argument to len not supported, got %s", arg.type()));
            }
        };
    }

    private String funcName;
    private Function<List<ValueObject>, ValueObject> function;

    BuiltinFunctionEnum(String funcName, Function function) {
        this.funcName = funcName;
        this.function = function;
    }

    /**
     * get builtin funtion of name
     * @param name
     * @return
     */
    public static BuiltinFunctionObject getBuiltinFunctionOf(String name) {
        return Arrays.stream(BuiltinFunctionEnum.values())
                .filter(item -> item.getFuncName().equals(name))
                .map(item -> item.getFunction())
                .map(item -> new BuiltinFunctionObject(item))
                .findFirst().orElse(null);


    }
}
