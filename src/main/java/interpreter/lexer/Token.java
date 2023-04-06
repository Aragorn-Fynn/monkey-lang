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

    public Token(TokenTypeEnum type) {
        this.type = type;
        this.literal = type.getLiterial();
    }
}
