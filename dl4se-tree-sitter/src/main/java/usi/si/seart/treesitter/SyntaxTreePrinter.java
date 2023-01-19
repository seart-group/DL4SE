package usi.si.seart.treesitter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Utility used for pretty-printing entire syntax trees, as well as their subtrees.
 */
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SyntaxTreePrinter {

    final Node node;
    String indentation = "  ";
    int currentLevel = 0;

    /**
     * @return A string representation of the subtree. Consists only of named nodes.
     * @see <a href="https://tree-sitter.github.io/tree-sitter/playground">Syntax Tree Playground</a>
     */
    public String printSubtree() {
        StringBuilder stringBuilder = new StringBuilder();
        @Cleanup TreeCursor cursor = new TreePrinterCursor(node);
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
                indentation.repeat(currentLevel),
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
            if (success) currentLevel++;
            return success;
        }

        @Override
        public boolean gotoParent() {
            boolean success = super.gotoParent();
            if (success) currentLevel--;
            return success;
        }
    }
}
