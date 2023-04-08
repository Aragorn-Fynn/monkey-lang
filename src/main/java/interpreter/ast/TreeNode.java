package interpreter.ast;

import java.io.Serializable;

/**
 * node interface, every implementation of TreeNode represents a type of syntax tree node, for example: statement, expression.
 * all the tree node implementations has only data. It's the input of interpreter,
 */
public interface TreeNode extends Serializable {
    String toString();
}
