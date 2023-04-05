package interpreter.object;

import interpreter.ast.TreeNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuoteObject implements ValueObject {

    private ValueTypeEnum type;
    private TreeNode node;

    public QuoteObject(TreeNode node) {
        this.node = node;
        this.type = ValueTypeEnum.QUOTE;
    }

    @Override
    public ValueTypeEnum type() {
        return this.type;
    }

    @Override
    public String inspect() {
        return "quote(" + node.toString() + ")";
    }
}
