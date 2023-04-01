package interpreter.lexer;

import lombok.Data;

/**
 * Top-Down Parsing
 * source string -> token
 */
@Data
public class Lexer {
    /**
     * source
      */
    private String input;

    /**
     * position being read
     */
    private int position;

    /**
     * position will be read in the future
     */
    private int readPosition;

    /**
     * current character being read
     */
    private char character;

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.readPosition = 1;
    }

    public Token nextToken() {
        char ch = consume();
        Token token;
        switch (ch) {
            case 0:
                token = new Token(TokenTypeEnum.EOF);
                break;
            case '=':
                if (peek() == '=') {
                    consume();
                    token = new Token(TokenTypeEnum.EQ);
                } else {
                    token = new Token(TokenTypeEnum.ASSIGN);
                }
                break;
            case '!':
                if (peek() == '=') {
                    consume();
                    token = new Token(TokenTypeEnum.NOT_EQ);
                } else {
                    token = new Token(TokenTypeEnum.BANG);
                }
                break;
            case '+':
                token = new Token(TokenTypeEnum.PLUS);
                break;
            case '-':
                token = new Token(TokenTypeEnum.MINUS);
                break;
            case '*':
                token = new Token(TokenTypeEnum.ASTERISK);
                break;
            case '/':
                token = new Token(TokenTypeEnum.SLASH);
                break;
            case '<':
                token = new Token(TokenTypeEnum.LT);
                break;
            case '>':
                token = new Token(TokenTypeEnum.GT);
                break;
            case ',':
                token = new Token(TokenTypeEnum.COMMA);
                break;
            case ';':
                token = new Token(TokenTypeEnum.SEMICOLON);
                break;
            case '(':
                token = new Token(TokenTypeEnum.LPAREN);
                break;
            case ')':
                token = new Token(TokenTypeEnum.RPAREN);
                break;
            case '{':
                token = new Token(TokenTypeEnum.LBRACE);
                break;
            case '}':
                token = new Token(TokenTypeEnum.RBRACE);
                break;
            default:
                if (Character.isDigit(ch)) {
                    token = getNum();
                } else if (isLetter(ch)) {
                    token = getIdent();
                } else {
                    token = new Token(TokenTypeEnum.ILLEGAL);
                }
        }

        return token;
    }

    /**
     * get number token
     * @return
     */
    private Token getNum() {
        int pos = position;
        String num = character + "";
        while (Character.isDigit(peek()) && !isEOF(peek())) {
            consume();
        }

        num += input.substring(pos, position);
        return new Token(TokenTypeEnum.INT, num);
    }

    /**
     * get identifier token
     * @return
     */
    private Token getIdent() {
        int pos = position;
        String identifier = character + "";
        while (isLetter(peek()) && !isEOF(peek())) {
            consume();
        }

        identifier += input.substring(pos, position);
        return new Token(TokenTypeEnum.of(identifier), identifier);
    }

    /**
     * if the current char is the end of file return 0
     * @param ch
     * @return
     */
    public boolean isEOF(char ch) {
        return ch == 0;
    }

    /**
     * if the current char is legal return true;
     * @param ch
     * @return
     */
    private boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
    }

    /**
     * consume current character
     * @return
     */
    private char consume() {
        if (position >= input.length()) {
            return 0;
        } else {
            character = input.charAt(position);
            position++;
            readPosition++;
            if (Character.isWhitespace(character)) {
                consume();
            }
            return character;
        }
    }

    /**
     * peek the next character
     * @return
     */
    private char peek() {
        if (position >= input.length()) {
            return 0;
        } else {
            return input.charAt(position);
        }
    }

}
