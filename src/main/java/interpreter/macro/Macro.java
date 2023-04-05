package interpreter.macro;

import interpreter.ast.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * support for macro
 */
@Data
@NoArgsConstructor
public class Macro {

    /**
     * suffix traverse, like eval
     * @param node
     * @param modifyFunc
     * @return
     */
    public static TreeNode modify(TreeNode node, Function<TreeNode, TreeNode> modifyFunc) {
        Class nodeClass = node.getClass();
        if (nodeClass.equals(ProgramNode.class)) {
            return modifyProgram((ProgramNode) node, modifyFunc);
        } else if (nodeClass.equals(ExpressionStatementNode.class)) {
            return modifyExpressionStatements((ExpressionStatementNode) node, modifyFunc);
        } else if (nodeClass.equals(BinaryExpressionNode.class)) {
            return modifyBinaryExpression((BinaryExpressionNode) node, modifyFunc);
        } else if (nodeClass.equals(UnaryExpressionNode.class)) {
            return modifyUnaryExpression((UnaryExpressionNode) node, modifyFunc);
        } else if (nodeClass.equals(IndexExpressionNode.class)) {
            return modifyIndexExpression((IndexExpressionNode) node, modifyFunc);
        } else if (nodeClass.equals(IfExpressionNode.class)) {
            return modifyIfExpression((IfExpressionNode) node, modifyFunc);
        } else if (nodeClass.equals(BlockStatement.class)) {
            return modifyBlockStatement((BlockStatement) node, modifyFunc);
        } else if (nodeClass.equals(ReturnStatementNode.class)) {
            return modifyReturnStatement((ReturnStatementNode) node, modifyFunc);
        } else if (nodeClass.equals(LetStatementNode.class)) {
            return modifyLetStatement((LetStatementNode) node, modifyFunc);
        } else if (nodeClass.equals(FunctionLiteralNode.class)) {
            return modifyFunctionLiteral((FunctionLiteralNode) node, modifyFunc);
        } else if (nodeClass.equals(ArrayLiteralExpressionNode.class)) {
            return modifyArrayLiteralExpression((ArrayLiteralExpressionNode) node, modifyFunc);
        } else if (nodeClass.equals(MapLiteralExpressionNode.class)) {
            return modifyMapLiteralExpression((MapLiteralExpressionNode) node, modifyFunc);
        }

        return modifyFunc.apply(node);
    }

    private static TreeNode modifyMapLiteralExpression(MapLiteralExpressionNode node, Function<TreeNode, TreeNode> modifyFunc) {
        Map<ExpressionNode, ExpressionNode> pairs = new HashMap<>();
        for (Map.Entry<ExpressionNode, ExpressionNode> entry : node.getPairs().entrySet()) {
            ExpressionNode key = (ExpressionNode) modify(entry.getKey(), modifyFunc);
            ExpressionNode value = (ExpressionNode) modify(entry.getValue(), modifyFunc);
            pairs.put(key, value);
        }
        node.setPairs(pairs);
        return node;
    }

    private static TreeNode modifyArrayLiteralExpression(ArrayLiteralExpressionNode node, Function<TreeNode, TreeNode> modifyFunc) {
        List<ExpressionNode> elements = new ArrayList<>();
        for (ExpressionNode ele :
                node.getElements()) {
            elements.add((ExpressionNode) modify(ele, modifyFunc));
        }

        node.setElements(elements);
        return node;
    }

    private static TreeNode modifyFunctionLiteral(FunctionLiteralNode node, Function<TreeNode, TreeNode> modifyFunc) {
        List<IdentifierNode> parameters = new ArrayList<>();
        for (IdentifierNode para : node.getParameters()) {
            parameters.add((IdentifierNode) modify(para, modifyFunc));
        }
        node.setParameters(parameters);

        node.setBody((BlockStatement) modify(node, modifyFunc));

        return node;
    }

    private static TreeNode modifyLetStatement(LetStatementNode node, Function<TreeNode, TreeNode> modifyFunc) {
        node.setValue((ExpressionNode) modify(node.getValue(), modifyFunc));
        return node;
    }

    private static TreeNode modifyReturnStatement(ReturnStatementNode node, Function<TreeNode, TreeNode> modifyFunc) {
        node.setValue((ExpressionNode) modify(node.getValue(), modifyFunc));
        return node;
    }

    private static TreeNode modifyBlockStatement(BlockStatement node, Function<TreeNode, TreeNode> modifyFunc) {
        List<StatementNode> statements = new ArrayList<>();
        for (StatementNode statement : node.getStatements()) {
            statements.add((StatementNode) modify(statement, modifyFunc));
        }

        node.setStatements(statements);
        return node;
    }

    private static TreeNode modifyIfExpression(IfExpressionNode node, Function<TreeNode, TreeNode> modifyFunc) {
        node.setCondition((ExpressionNode) modify(node.getCondition(), modifyFunc));
        node.setConsequence((BlockStatement) modify(node.getConsequence(), modifyFunc));
        if (node.getAlternative() != null) {
            node.setAlternative((BlockStatement) modify(node.getAlternative(), modifyFunc));
        }

        return node;
    }

    private static TreeNode modifyIndexExpression(IndexExpressionNode node, Function<TreeNode, TreeNode> modifyFunc) {
        node.setObject((ExpressionNode) modify(node.getObject(), modifyFunc));
        node.setIndex((ExpressionNode) modify(node.getIndex(), modifyFunc));
        return node;
    }

    private static TreeNode modifyUnaryExpression(UnaryExpressionNode node, Function<TreeNode, TreeNode> modifyFunc) {
        node.setRight((ExpressionNode) modify(node.getRight(), modifyFunc));
        return node;
    }

    private static TreeNode modifyBinaryExpression(BinaryExpressionNode node, Function<TreeNode, TreeNode> modifyFunc) {
        node.setLeft((ExpressionNode) modify(node.getLeft(), modifyFunc));
        node.setRight((ExpressionNode) modify(node.getRight(), modifyFunc));
        return node;
    }



    /**
     * modify child nodes, and then modify the node;
     * @param node
     * @param modifyFunc
     * @return
     */
    private static TreeNode modifyProgram(ProgramNode node, Function<TreeNode, TreeNode> modifyFunc) {
        List<StatementNode> statements = node.getStatements();
        for (StatementNode statement : node.getStatements()) {
            statements.add((StatementNode) modify(statement, modifyFunc));
        }
        node.setStatements(statements);
        return modifyFunc.apply(node);
    }

    private static TreeNode modifyExpressionStatements(ExpressionStatementNode node, Function<TreeNode, TreeNode> modifyFunc) {
        ExpressionNode expression = (ExpressionNode) modify(node.getExpression(), modifyFunc);
        node.setExpression(expression);
        return modifyFunc.apply(node);
    }
}
