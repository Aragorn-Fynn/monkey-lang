package interpreter.builtin;

import interpreter.object.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Getter
public enum BuiltinFunctionEnum {

    LEN("len", lenFunction()),
    FIRST("first", firstFunction()),
    PUSH("push", pushFunction()),
    PUT("put", putFunction()),
    PRINT("print", printFunction()),
    TIME("time", timeFunction());

    private static Function<List<ValueObject>, ValueObject> putFunction() {
        return args -> {
            if (args == null || args.size() != 3) {
                return new ErrorObject(String.format("wrong number of arguments, got=%d, want=3", args.size()));
            }

            ValueObject arg = args.get(0);
            if (arg.type() != ValueTypeEnum.MAP) {
                return new ErrorObject(String.format("argument to push must be MAP, got %s", arg.type()));
            }

            Map<ValueObject, ValueObject> copyMap = new HashMap<>(((MapObject) arg).getPairs());
            copyMap.put(args.get(1), args.get(2));

            MapObject res = new MapObject();
            res.setPairs(copyMap);
            return res;
        };
    }

    /**
     * print time with format yyyy-MM-dd HH:mm:ss
     * @return
     */
    private static Function<List<ValueObject>, ValueObject> timeFunction() {
        return args -> {
            if (args == null || args.size() != 0) {
                return new ErrorObject(String.format("wrong number of arguments, got=%d, want=0", args.size()));
            }

            return new StringObject(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        };
    }


    /**
     * print all value
     * @return
     */
    private static Function<List<ValueObject>, ValueObject> printFunction() {
        return args -> {
            for (ValueObject arg : args) {
                System.out.println(arg.inspect());
            }

            return null;
        };
    }

    /**
     * array is immutable
     * push an element to array, return a new array
     * @return
     */
    private static Function<List<ValueObject>, ValueObject> pushFunction() {
        return args -> {
            if (args == null || args.size() != 2) {
                return new ErrorObject(String.format("wrong number of arguments, got=%d, want=1", args.size()));
            }

            ValueObject arg = args.get(0);
            if (arg.type() != ValueTypeEnum.ARRAY) {
                return new ErrorObject(String.format("argument to push must be ARRAY, got %s", arg.type()));
            }

            List<ValueObject> copyArray = new ArrayList<>(((ArrayObject) arg).getElements());
            copyArray.add(args.get(1));

            return new ArrayObject(copyArray);
        };
    }

    /**
     * get first element of array
     * @return
     */
    private static Function<List<ValueObject>, ValueObject> firstFunction() {
        return args -> {
            if (args == null || args.size() != 1) {
                return new ErrorObject(String.format("wrong number of arguments, got=%d, want=1", args.size()));
            }

            ValueObject arg = args.get(0);
            switch (arg.type()) {
                case ARRAY:
                    return ((ArrayObject) arg).getElements().get(0);
                default:
                    return new ErrorObject(String.format("argument to first not supported, got %s", arg.type()));
            }
        };
    }

    /**
     * get length of array or string
     * @return
     */
    private static Function<List<ValueObject>, ValueObject> lenFunction() {
        return args -> {
            if (args == null || args.size() != 1) {
                return new ErrorObject(String.format("wrong number of arguments, got=%d, want=1", args.size()));
            }

            ValueObject arg = args.get(0);
            switch (arg.type()) {
                case MAP:
                    return new IntegerObject(((MapObject) arg).getPairs().size());
                case ARRAY:
                    return new IntegerObject(((ArrayObject) arg).getElements().size());
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
