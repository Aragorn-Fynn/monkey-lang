package interpreter.lexer;

import lombok.Getter;

import java.util.Arrays;

/**
 * Token Type: remember the kind of token while scanning is convenient for parsing.
 */
@Getter
public enum TokenTypeEnum {
    ILLEGAL("ILLEGAL", false),
    EOF("EOF", false),

    // identifier
    IDENT("IDENT", false),

    // Integer
    INT("INT", false),

    // string
    STRING("STRING", false),

    // operator
    ASSIGN("=", false),
    PLUS("+", false),
    MINUS("-", false),
    BANG("!", false),
    ASTERISK("*", false),
    SLASH("/", false),

    LT("<", false),
    LE("<=", false),
    GT(">", false),
    GE(">=", false),

    EQ("==", false),
    NOT_EQ("!=", false),

    // seperator
    COMMA(",", false),
    SEMICOLON(";", false),
    COLON(":", false),

    LPAREN("(", false),
    RPAREN(")", false),
    LBRACE("{", false),
    RBRACE("}", false),
    // array token
    LBRACKET("[", false),
    RBRACKET("]", false),

    // keywords
    FUNCTION("fn", true),
    LET("let", true),
    TRUE("true", true),
    FALSE("false", true),
    IF("if", true),
    ELSE("else", true),
    RETURN("return", true),
    MACRO("macro", true),
    WHILE("while", true),
    AND("and", true),
    OR("or", true)
    ;


    private String literial;

    private boolean isKeyWord;

    TokenTypeEnum(String literial, boolean isKeyWord) {
        this.literial = literial;
        this.isKeyWord = isKeyWord;
    }

    public static TokenTypeEnum of(String identifier) {
        TokenTypeEnum keyWord = Arrays.stream(TokenTypeEnum.values())
                .filter(item -> item.isKeyWord())
                .filter(item -> item.getLiterial().equals(identifier))
                .findFirst().orElse(null);

        return keyWord != null ? keyWord : TokenTypeEnum.IDENT;
    }
}
