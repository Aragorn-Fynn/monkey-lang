package interpreter.lexer;

/**
 * Token Type
 */
public enum TokenTypeEnum {
    ILLEGAL("ILLEGAL"),
    EOF("EOF"),

    // 标识符
    IDENT("IDENT"),
    // Integer
    INT("INT"),
    // operator
    ASSIGN("="),
    PLUS("+"),

    // seperator
    COMMA(","),
    SEMICOLON(";"),

    LPAREN("("),
    RPAREN(")"),
    LBRACE("{"),
    RBRACE("}"),

    // key word
    FUNCTION("FUNCTION"),
    LET("LET");
    private String type;

    TokenTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
