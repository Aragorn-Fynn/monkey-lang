package interpreter.ast;

import interpreter.lexer.Token;
import lombok.Data;

/**
 * node class of assign statemnet;
 * example: a=1;
 */
@Data
public class AssignExpressionNode implements ExpressionNode {
    Token token;

    private IdentifierNode name;

    private ExpressionNode value;

    @Override
    public String toString() {
        return (name == null ? "" : name.toString()) + " = "
                + (value == null ? "" : value.toString())+";";
    }
}
