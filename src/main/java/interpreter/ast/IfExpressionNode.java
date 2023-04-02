package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * if expression;
 * example: if(true) {return true} else {return false;}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IfExpressionNode implements ExpressionNode {
    private Token token;
    private ExpressionNode condition;

    private BlockStatement consequence;
    private BlockStatement alternative;

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer("if");
        res.append(condition.toString());
        res.append(" ");
        res.append(consequence.toString());

        if (alternative != null) {
            res.append("else ").append(alternative.toString());
        }

        return res.toString();
    }
}
