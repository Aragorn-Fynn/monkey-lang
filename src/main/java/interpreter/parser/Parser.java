package interpreter.parser;

import interpreter.ast.*;
import interpreter.lexer.Lexer;
import interpreter.lexer.Token;
import interpreter.lexer.TokenTypeEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 语法分析器：token -> ast, using Recursive Down, Pratt Parsing
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

    /**
     * the function of parsing prefix expression;
     */
    private Map<TokenTypeEnum, Supplier<ExpressionNode>> prefixParseFuncMap = new HashMap<>();

    /**
     * the function of parsing infix expression;
     */
    private Map<TokenTypeEnum, Function<ExpressionNode, ExpressionNode>> infixParseFuncMap = new HashMap<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        //read two token, init currentToken and peekToken
        consume();
        consume();

        initParseFuncMap();
    }

    private void initParseFuncMap() {
        prefixParseFuncMap.put(TokenTypeEnum.IDENT, () -> new IdentifierNode(currentToken, currentToken.getLiteral()));
        prefixParseFuncMap.put(TokenTypeEnum.INT, () -> {
            IntegerLiteralNode res = new IntegerLiteralNode();
            res.setToken(currentToken);
            try {
                Integer value = Integer.valueOf(currentToken.getLiteral());
                res.setValue(value);
            } catch (Exception e) {
                errors.add(String.format("could not parse %s as integer", currentToken.getLiteral()));
                return null;
            }
            return res;
        });
        Supplier<ExpressionNode> unaryExpressionParseFunc = () -> {
            UnaryExpressionNode res = new UnaryExpressionNode();
            res.setToken(currentToken);
            res.setOperator(currentToken.getLiteral());

            consume();
            res.setRight(parseExpression(PrecedenceEnum.PREFIX));
            return res;
        };
        prefixParseFuncMap.put(TokenTypeEnum.BANG, unaryExpressionParseFunc);
        prefixParseFuncMap.put(TokenTypeEnum.MINUS, unaryExpressionParseFunc);
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
                statement = parseExpressionStatement();
        }

        return statement;
    }

    private StatementNode parseExpressionStatement() {
        ExpressionStatementNode statement = new ExpressionStatementNode();
        statement.setToken(currentToken);
        statement.setExpression(parseExpression(PrecedenceEnum.LOWEST));

        expectPeek(TokenTypeEnum.SEMICOLON);

        return statement;
    }

    private StatementNode parseReturnStatement() {
        ReturnStatementNode returnStatement = new ReturnStatementNode();
        returnStatement.setToken(currentToken);
        consume();
        returnStatement.setExpression(parseExpression(PrecedenceEnum.LOWEST));
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

        letStatement.setExpression(parseExpression(PrecedenceEnum.LOWEST));

        while (currentToken.getType() != TokenTypeEnum.SEMICOLON) {
            consume();
        }

        return letStatement;
    }

    // TODO parse Expression
    private ExpressionNode parseExpression(PrecedenceEnum lowest) {
        Supplier<ExpressionNode> prefixFunc = prefixParseFuncMap.get(currentToken.getType());
        if (prefixFunc == null) {
            errors.add(String.format("no prefix parse function for %s found", currentToken.getType().getLiterial()));
            return null;
        }
        ExpressionNode leftExpression = prefixFunc.get();
        return leftExpression;
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
        Lexer lexer1 = new Lexer("foobar;");
        Parser parser = new Parser(lexer1);
        ProgramNode programNode = parser.parseProgram();
        System.out.println(programNode.toString());
    }
}
