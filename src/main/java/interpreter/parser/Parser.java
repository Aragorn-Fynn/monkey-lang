package interpreter.parser;

import interpreter.ast.*;
import interpreter.lexer.Lexer;
import interpreter.lexer.Token;
import interpreter.lexer.TokenTypeEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 语法分析器：token -> ast
 */
@Data
public class Parser {
    /**
     * 词法分析器
     */
    private Lexer lexer;

    /**
     * current reading token
     */
    private Token currentToken;

    /**
     * token will be read
     */
    private Token peekToken;

    /**
     * error message while parse
     */
    private List<String> errors = new ArrayList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        //read two token, init currentToken and peekToken
        consume();
        consume();
    }

    /**
     * parse the program
     * @return
     */
    public ProgramNode parseProgram() {
        ProgramNode programNode = new ProgramNode();
        List<StatementNode> statements = parseStatements();
        programNode.setStatements(statements);
        return programNode;
    }

    /**
     * parse statements
     * @return
     */
    private List<StatementNode> parseStatements() {
        List<StatementNode> statements = new ArrayList<>();
        while (currentToken.getType() != TokenTypeEnum.EOF) {
            StatementNode statement = parseStatement();
            if (statement != null) {
                statements.add(statement);
            }

            consume();
        }
        return statements;
    }

    private StatementNode parseStatement() {
        StatementNode statement;
        switch (currentToken.getType()) {
            case LET:
                statement = parseLetStatement();
                break;
            case RETURN:
                statement = parseReturnStatement();
                break;
            default:
                statement = null;
        }

        return statement;
    }

    private StatementNode parseReturnStatement() {
        ReturnStatementNode returnStatement = new ReturnStatementNode();
        returnStatement.setToken(currentToken);
        returnStatement.setExpression(parseExpression());
        while (currentToken.getType() != TokenTypeEnum.SEMICOLON) {
            consume();
        }
        return returnStatement;
    }

    /**
     * parse let statement
     */
    private StatementNode parseLetStatement() {
        LetStatementNode letStatement = new LetStatementNode();
        letStatement.setToken(currentToken);
        letStatement.setIdentifier(parseIdentifiter());

        expectPeek(TokenTypeEnum.ASSIGN);

        letStatement.setExpression(parseExpression());

        while (currentToken.getType() != TokenTypeEnum.SEMICOLON) {
            consume();
        }

        return letStatement;
    }

    // TODO parse Expression
    private ExpressionNode parseExpression() {
        return null;
    }

    private IdentifierNode parseIdentifiter() {
        if (expectPeek(TokenTypeEnum.IDENT)) {
            return new IdentifierNode(currentToken, currentToken.getLiteral());
        } else {
            return null;
        }
    }

    private boolean expectPeek(TokenTypeEnum type) {
        if (peekToken.getType() == type) {
            consume();
            return true;
        } else {
            peekError(type);
            return false;
        }
    }

    private void peekError(TokenTypeEnum type) {
        String error = String.format("expected next token to be %s, but got %s instead!", type, peekToken.getType());
        errors.add(error);
    }

    /**
     * read next token
     */
    private void consume() {
        this.currentToken = peekToken;
        this.peekToken = lexer.nextToken();
    }

    public static void main(String[] args) {
        Lexer lexer1 = new Lexer("return 5;");
        Parser parser = new Parser(lexer1);
        ProgramNode programNode = parser.parseProgram();
        System.out.println(programNode.toString());
    }
}
