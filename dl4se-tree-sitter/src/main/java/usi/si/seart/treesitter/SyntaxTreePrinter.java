package usi.si.seart.treesitter;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Utility used for pretty-printing entire syntax trees, as well as their subtrees.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SyntaxTreePrinter {

    int depth = 0;
    final Node node;

    /**
     * @return A string representation of the subtree. Consists only of named nodes.
     * @see <a href="https://tree-sitter.github.io/tree-sitter/playground">Syntax Tree Playground</a>
     */
    public String printSubtree() {
        StringBuilder stringBuilder = new StringBuilder();
        @Cleanup TreeCursor cursor = new TreePrinterCursor(node);
        for (;;) {
            TreeCursorNode cursorNode = cursor.getCurrentTreeCursorNode();
            if (cursorNode.isNamed()) {
                String indentation = "  ".repeat(depth);
                stringBuilder
                        .append(indentation)
                        .append(cursorNode)
                        .append("\n");
            }
            if (cursor.gotoFirstChild() || cursor.gotoNextSibling()) continue;
            do {
                if (!cursor.gotoParent()) return stringBuilder.toString();
            } while (!cursor.gotoNextSibling());
        }
    }

    private final class TreePrinterCursor extends TreeCursor {

        private TreePrinterCursor(Node node) {
            super(TreeSitter.treeCursorNew(node));
        }

        @Override
        public boolean gotoFirstChild() {
            boolean success = super.gotoFirstChild();
            if (success) depth++;
            return success;
        }

        @Override
        public boolean gotoParent() {
            boolean success = super.gotoParent();
            if (success) depth--;
            return success;
        }
    }
}
