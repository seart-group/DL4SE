package usi.si.seart.treesitter;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Utility used for pretty-printing parse trees.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TreePrinter {

    int indentLevel = 0;
    final Tree tree;

    /**
     * @return A string representation of the parse tree. Consists only of named nodes.
     */
    public String printParseTree() {
        StringBuilder stringBuilder = new StringBuilder();
        Node root = this.tree.getRootNode();
        @Cleanup TreeCursor cursor = new TreePrinterCursor(root);
        for (;;) {
            TreeCursorNode treeCursorNode = cursor.getCurrentTreeCursorNode();
            if (treeCursorNode.isNamed()) {
                String treeNode = printTreeNode(treeCursorNode);
                stringBuilder.append(treeNode);
            }
            if (cursor.gotoFirstChild() || cursor.gotoNextSibling()) continue;
            do {
                if (!cursor.gotoParent()) return stringBuilder.toString();
            } while (!cursor.gotoNextSibling());
        }
    }

    private String printTreeNode(TreeCursorNode treeCursorNode) {
        String name = treeCursorNode.getName();
        String type = treeCursorNode.getType();
        Point startPoint = treeCursorNode.getStartPoint();
        Point endPoint = treeCursorNode.getEndPoint();
        return String.format("%s%s%s [%s] - [%s]%n",
                "  ".repeat(this.indentLevel),
                (name != null) ? name + ": " : "",
                type, startPoint, endPoint
        );
    }

    private final class TreePrinterCursor extends TreeCursor {

        private TreePrinterCursor(Node node) {
            super(TreeSitter.treeCursorNew(node));
        }

        @Override
        public boolean gotoFirstChild() {
            boolean success = super.gotoFirstChild();
            if (success) indentLevel++;
            return success;
        }

        @Override
        public boolean gotoParent() {
            boolean success = super.gotoParent();
            if (success) indentLevel--;
            return success;
        }
    }
}
