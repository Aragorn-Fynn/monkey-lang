package interpreter.ast;

import interpreter.lexer.Token;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * while expression node
 * example: while condition {body}
 * @author: chengfei.feng
 * date: 2023/4/7 16:59
 * description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhileExpressionNode implements ExpressionNode {

        private Token token;

        private ExpressionNode condition;

        private BlockStatement body;


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("while").append(" ");
            sb.append(condition.toString()).append(" ");
            sb.append(body.toString());
            return sb.toString();
        }
}
