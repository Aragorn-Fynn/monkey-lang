package interpreter.lexer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * token： output of lexer, input of parser
 */
@Data
@AllArgsConstructor
public class Token implements Serializable {
    /**
     * token type
     */
    private TokenTypeEnum type;

    /**
     * literal of token
     */
    private String literal;

    /**
     * the line in the source
     */
    private Integer line;

    public Token(TokenTypeEnum type, Integer line) {
        this.type = type;
        this.line = line;
        this.literal = type.getLiterial();
    }
}
