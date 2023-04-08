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

    /**
     * current line of source
     */
    private int line;

    private static final Token EOF = new Token(TokenTypeEnum.EOF, -1);

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.readPosition = 1;
        this.line = 1;
    }

    public Token nextToken() {
        char ch = consume();
        Token token;
        switch (ch) {
            case 0:
                token = EOF;
                break;
            case '=':
                if (peek() == '=') {
                    consume();
                    token = new Token(TokenTypeEnum.EQ, line);
                } else {
                    token = new Token(TokenTypeEnum.ASSIGN, line);
                }
                break;
            case '!':
                if (peek() == '=') {
                    consume();
                    token = new Token(TokenTypeEnum.NOT_EQ, line);
                } else {
                    token = new Token(TokenTypeEnum.BANG, line);
                }
                break;
            case '+':
                token = new Token(TokenTypeEnum.PLUS, line);
                break;
            case '-':
                token = new Token(TokenTypeEnum.MINUS, line);
                break;
            case '*':
                token = new Token(TokenTypeEnum.ASTERISK, line);
                break;
            case '/':
                token = new Token(TokenTypeEnum.SLASH, line);
                break;
            case '<':
                if (peek() == '=') {
                    consume();
                    token = new Token(TokenTypeEnum.LE, line);
                } else {
                    token = new Token(TokenTypeEnum.LT, line);
                }
                break;
            case '>':
                if (peek() == '=') {
                    consume();
                    token = new Token(TokenTypeEnum.GE, line);
                } else {
                    token = new Token(TokenTypeEnum.GT, line);
                }
                break;
            case ',':
                token = new Token(TokenTypeEnum.COMMA, line);
                break;
            case ';':
                token = new Token(TokenTypeEnum.SEMICOLON, line);
                break;
            case '(':
                token = new Token(TokenTypeEnum.LPAREN, line);
                break;
            case ')':
                token = new Token(TokenTypeEnum.RPAREN, line);
                break;
            case '{':
                token = new Token(TokenTypeEnum.LBRACE, line);
                break;
            case '}':
                token = new Token(TokenTypeEnum.RBRACE, line);
                break;
            case '[':
                token = new Token(TokenTypeEnum.LBRACKET, line);
                break;
            case ']':
                token = new Token(TokenTypeEnum.RBRACKET, line);
                break;
            case '"':
                token = getString();
                break;
            case ':' :
                token = new Token(TokenTypeEnum.COLON, line);
                break;
            default:
                if (Character.isDigit(ch)) {
                    token = getNum();
                } else if (isAlpha(ch)) {
                    token = getIdent();
                } else {
                    token = new Token(TokenTypeEnum.ILLEGAL, line);
                }
        }

        return token;
    }

    /**
     * get string token
     * @return
     */
    private Token getString() {
        int pos = position;
        consume();
        while ('"' != character && 0 != character) {
            consume();
        }

        String str = input.substring(pos, position - 1);
        return new Token(TokenTypeEnum.STRING, str, line);
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
        return new Token(TokenTypeEnum.INT, num, line);
    }

    /**
     * get identifier token
     * @return
     */
    private Token getIdent() {
        int pos = position;
        String identifier = character + "";
        while ((isAlpha(peek()) || Character.isDigit(peek()))&& !isEOF(peek())) {
            consume();
        }

        identifier += input.substring(pos, position);
        return new Token(TokenTypeEnum.of(identifier), identifier, line);
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
    private boolean isAlpha(char ch) {
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
                if (character == '\n') {
                    line++;
                }
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
