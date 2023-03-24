package interpreter.lexer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * token
 */
@Data
@AllArgsConstructor
public class Token {
    /**
     * token类型
     */
    private TokenTypeEnum type;

    /**
     * 字面值
     */
    private String literal;

    public Token(TokenTypeEnum type) {
        this.type = type;
        this.literal = type.getLiterial();
    }
}
