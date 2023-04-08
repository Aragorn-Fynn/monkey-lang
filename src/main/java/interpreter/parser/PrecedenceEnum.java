package interpreter.parser;

/**
 * precedence
 */
public enum PrecedenceEnum {
    DEFAULT,
    LOWEST,
    // =
    ASSIGN,
    // or
    OR,
    // and
    AND,
    // == !=
    EQUALS,
    // > < >= <=
    LESSGREATER,
    // + -
    SUM,
    // * /
    PRODUCT,
    // -x or !x
    PREFIX,
    // function(x)
    CALL,
    // arr[1]
    INDEX;

    public static boolean higherPrecedenceThan(PrecedenceEnum first, PrecedenceEnum second) {
        return first.ordinal() > second.ordinal();
    }
}