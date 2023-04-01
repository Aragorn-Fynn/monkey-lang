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
 * parser：token -> ast, using Recursive Down, Pratt Parsing
 */
@Data
public class Parser {
    /**
     * read token from lexer
     */
    private Lexer lexer;

    /**
     * current reading token
     */
    private Token currentToken;

    /**
     * token will be read in the future
     */
    private Token peekToken;

    /**
     * errors occurred while parsing
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

    /**
     * precedence of operator
     */
    private Map<TokenTypeEnum, PrecedenceEnum> type2PrecedenceMap = new HashMap<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        //read two token, init currentToken and peekToken
        consume();
        consume();

        initPrecedenceMap();
        initParseFuncMap();
    }

    private void initPrecedenceMap() {
        type2PrecedenceMap.put(TokenTypeEnum.EQ, PrecedenceEnum.EQUALS);
        type2PrecedenceMap.put(TokenTypeEnum.NOT_EQ, PrecedenceEnum.EQUALS);
        type2PrecedenceMap.put(TokenTypeEnum.LT, PrecedenceEnum.LESSGREATER);
        type2PrecedenceMap.put(TokenTypeEnum.GT, PrecedenceEnum.LESSGREATER);
        type2PrecedenceMap.put(TokenTypeEnum.PLUS, PrecedenceEnum.SUM);
        type2PrecedenceMap.put(TokenTypeEnum.MINUS, PrecedenceEnum.SUM);
        type2PrecedenceMap.put(TokenTypeEnum.ASTERISK, PrecedenceEnum.PRODUCT);
        type2PrecedenceMap.put(TokenTypeEnum.SLASH, PrecedenceEnum.PRODUCT);
        type2PrecedenceMap.put(TokenTypeEnum.LPAREN, PrecedenceEnum.CALL);
    }

    /**
     * get the precedence of next token
     * @return
     */
    private PrecedenceEnum peekPrecedence() {
        if (type2PrecedenceMap.containsKey(peekToken.getType())) {
            return type2PrecedenceMap.get(peekToken.getType());
        } else {
            return PrecedenceEnum.LOWEST;
        }
    }

    /**
     * get the precedence of current token
     * @return
     */
    private PrecedenceEnum currentPrecedence() {
        if (type2PrecedenceMap.containsKey(currentToken.getType())) {
            return type2PrecedenceMap.get(currentToken.getType());
        } else {
            return PrecedenceEnum.LOWEST;
        }
    }

    private void initParseFuncMap() {
        initPrefixParseFuncMap();
        initInfixParseFuncMap();

    }

    /**
     * init the function of parsing infix expression
     */
    private void initInfixParseFuncMap() {

        infixParseFuncMap.put(TokenTypeEnum.PLUS, infixparseFunc());
        infixParseFuncMap.put(TokenTypeEnum.MINUS, infixparseFunc());
        infixParseFuncMap.put(TokenTypeEnum.SLASH, infixparseFunc());
        infixParseFuncMap.put(TokenTypeEnum.ASTERISK, infixparseFunc());

        infixParseFuncMap.put(TokenTypeEnum.GT, infixparseFunc());
        infixParseFuncMap.put(TokenTypeEnum.LT, infixparseFunc());
        infixParseFuncMap.put(TokenTypeEnum.EQ, infixparseFunc());
        infixParseFuncMap.put(TokenTypeEnum.NOT_EQ, infixparseFunc());

        infixParseFuncMap.put(TokenTypeEnum.LPAREN, parseCallFunc());
    }

    private Function<ExpressionNode, ExpressionNode> parseCallFunc() {
        return left -> {
            CallExpressionNode res = new CallExpressionNode();
            res.setToken(currentToken);
            res.setFuncName(left);
            res.setArguments(parseCallArguments());
            return res;
        };
    }

    private List<ExpressionNode> parseCallArguments() {
        List<ExpressionNode> arguments = new ArrayList<>();
        if (peekToken.getType() == TokenTypeEnum.RPAREN) {
            consume();
            return arguments;
        }

        consume();
        arguments.add(parseExpression(PrecedenceEnum.LOWEST));
        while (peekToken.getType() == TokenTypeEnum.COMMA) {
            consume();
            consume();
            arguments.add(parseExpression(PrecedenceEnum.LOWEST));
        }

        if (!expectPeek(TokenTypeEnum.RPAREN)) {
            return null;
        }

        return arguments;
    }

    private Function<ExpressionNode, ExpressionNode> infixparseFunc() {
        Function<ExpressionNode, ExpressionNode> infixparseFunc = left -> {
            BinaryExpressionNode res = new BinaryExpressionNode();
            res.setToken(currentToken);
            res.setLeft(left);
            res.setOperator(currentToken.getLiteral());

            PrecedenceEnum precedence = currentPrecedence();
            consume();
            res.setRight(parseExpression(precedence));
            return res;
        };
        return infixparseFunc;
    }

    private void initPrefixParseFuncMap() {
        prefixParseFuncMap.put(TokenTypeEnum.IDENT, () -> new IdentifierNode(currentToken, currentToken.getLiteral()));
        prefixParseFuncMap.put(TokenTypeEnum.INT, intExpressionParseFunc());
        prefixParseFuncMap.put(TokenTypeEnum.LPAREN, groupedExpressionParseFunc());
        prefixParseFuncMap.put(TokenTypeEnum.TRUE, () -> new BooleanLiteralNode(currentToken, true));
        prefixParseFuncMap.put(TokenTypeEnum.FALSE, () -> new BooleanLiteralNode(currentToken, false));
        prefixParseFuncMap.put(TokenTypeEnum.BANG, unaryExpressionParseFunc());
        prefixParseFuncMap.put(TokenTypeEnum.MINUS, unaryExpressionParseFunc());
        // add function of parsing string
        prefixParseFuncMap.put(TokenTypeEnum.STRING, () -> new StringLiteralNode(currentToken, currentToken.getLiteral()));

        prefixParseFuncMap.put(TokenTypeEnum.IF, ifExpressionParseFunc());
        prefixParseFuncMap.put(TokenTypeEnum.FUNCTION, functionparseFunc());


    }

    private Supplier<ExpressionNode> functionparseFunc() {
        return () -> {
            FunctionLiteralNode res = new FunctionLiteralNode();
            res.setToken(currentToken);
            if (!expectPeek(TokenTypeEnum.LPAREN)) {
                return null;
            }

            res.setParameters(parseParameters());

            if (!expectPeek(TokenTypeEnum.LBRACE)) {
                return null;
            }

            res.setBody(parseBlockStatement());

            return res;
        };
    }

    private List<IdentifierNode> parseParameters() {
        List<IdentifierNode> res = new ArrayList<>();
        if (peekToken.getType() == TokenTypeEnum.RPAREN) {
            consume();
            return res;
        }

        consume();
        res.add(new IdentifierNode(currentToken, currentToken.getLiteral()));
        while (peekToken.getType() == TokenTypeEnum.COMMA) {
            consume();
            consume();
            res.add(new IdentifierNode(currentToken, currentToken.getLiteral()));
        }

        if (!expectPeek(TokenTypeEnum.RPAREN)) {
            return null;
        }

        return res;
    }
    private Supplier<ExpressionNode> unaryExpressionParseFunc() {
        Supplier<ExpressionNode> unaryExpressionParseFunc = () -> {
            UnaryExpressionNode res = new UnaryExpressionNode();
            res.setToken(currentToken);
            res.setOperator(currentToken.getLiteral());

            consume();
            res.setRight(parseExpression(PrecedenceEnum.PREFIX));
            return res;
        };
        return unaryExpressionParseFunc;
    }

    private Supplier<ExpressionNode> groupedExpressionParseFunc() {
        return () -> {
            consume();
            ExpressionNode expression = parseExpression(PrecedenceEnum.LOWEST);
            if (!expectPeek(TokenTypeEnum.RPAREN)) {
                return null;
            }

            return expression;
        };
    }

    private Supplier<ExpressionNode> intExpressionParseFunc() {
        return () -> {
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
        };
    }

    private Supplier<ExpressionNode> ifExpressionParseFunc() {
        return () -> {
            IfExpressionNode res = new IfExpressionNode();
            res.setToken(currentToken);
            if (!expectPeek(TokenTypeEnum.LPAREN)) {
                return null;
            }

            consume();
            res.setCondition(parseExpression(PrecedenceEnum.LOWEST));
            if (!expectPeek(TokenTypeEnum.RPAREN)) {
                return null;
            }

            if (!expectPeek(TokenTypeEnum.LBRACE)) {
                return null;
            }
            res.setConsequence(parseBlockStatement());

            if (peekToken.getType() == TokenTypeEnum.ELSE) {
                consume();
                if (!expectPeek(TokenTypeEnum.LBRACE)) {
                    return null;
                }

                res.setAlternative(parseBlockStatement());
            }

            return res;
        };
    }

    private BlockStatement parseBlockStatement() {
        BlockStatement statements = new BlockStatement();
        statements.setToken(currentToken);
        consume();
        while (currentToken.getType() != TokenTypeEnum.RBRACE && currentToken.getType() != TokenTypeEnum.EOF) {
            StatementNode statement = parseStatement();
            if (statement != null) {
                statements.getStatements().add(statement);
            }
            consume();
        }

        return statements;
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

    /**
     * parse statement, monkey lang has three type of statements: let/return/expression
     * @return
     */
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

        // next token should be semicolon, not must
        if (peekToken.getType() == TokenTypeEnum.SEMICOLON) {
            consume();
        }

        return statement;
    }

    private StatementNode parseReturnStatement() {
        ReturnStatementNode returnStatement = new ReturnStatementNode();
        // current token is return
        returnStatement.setToken(currentToken);
        // consume return token
        consume();
        // next is an expression
        returnStatement.setValue(parseExpression(PrecedenceEnum.LOWEST));

        if (peekToken.getType() == TokenTypeEnum.SEMICOLON) {
            consume();
        }
        return returnStatement;
    }

    /**
     * parse let statement
     */
    private StatementNode parseLetStatement() {
        LetStatementNode letStatement = new LetStatementNode();
        // current token is let
        letStatement.setToken(currentToken);
        // next token should be identifier
        letStatement.setName(parseIdentifiter());

        // next token should be =
        expectPeek(TokenTypeEnum.ASSIGN);
        // consume =
        consume();
        // next is an expression
        letStatement.setValue(parseExpression(PrecedenceEnum.LOWEST));

        if (peekToken.getType() == TokenTypeEnum.SEMICOLON) {
            consume();
        }

        return letStatement;
    }

    /**
     * parse expression with pratt algorithm
     * 1. parse prefix with registered function
     * 2. if the precedence of peek token is higher than current token, parse infix expression with registered function
     * 3. if not, return the expression of step 1
     * @param precedence
     * @return
     */
    private ExpressionNode parseExpression(PrecedenceEnum precedence) {
        Supplier<ExpressionNode> prefixFunc = prefixParseFuncMap.get(currentToken.getType());
        if (prefixFunc == null) {
            errors.add(String.format("no prefix parse function for %s found", currentToken.getType().getLiterial()));
            return null;
        }
        ExpressionNode leftExpression = prefixFunc.get();

        while (!(peekToken.getType() == TokenTypeEnum.SEMICOLON)
                && PrecedenceEnum.higherPrecedenceThan(peekPrecedence(), precedence)) {
            Function<ExpressionNode, ExpressionNode> parseFunc = infixParseFuncMap.get(peekToken.getType());
            if (parseFunc == null) {
                return leftExpression;
            }

            consume();

            leftExpression = parseFunc.apply(leftExpression);
        }
        return leftExpression;
    }

    private IdentifierNode parseIdentifiter() {
        if (expectPeek(TokenTypeEnum.IDENT)) {
            return new IdentifierNode(currentToken, currentToken.getLiteral());
        } else {
            return null;
        }
    }

    /**
     * 1. if next token matches the expected token, consume and return true.
     * 2. if not, return false;
     * @param type
     * @return
     */
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

}