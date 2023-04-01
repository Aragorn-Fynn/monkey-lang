package interpreter.parser;

/**
 * precedence
 */
public enum PrecedenceEnum {
    DEFAULT,
    LOWEST,
    // ==
    EQUALS,
    // > or <
    LESSGREATER,
    // +
    SUM,
    // *
    PRODUCT,
    // -x or !x
    PREFIX,
    // function(x)
    CALL;

    public static boolean higherPrecedenceThan(PrecedenceEnum first, PrecedenceEnum second) {
        return first.ordinal() > second.ordinal();
    }
}