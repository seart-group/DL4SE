package ch.usi.si.seart.analyzer.hash;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.TreeCursor;
import lombok.Cleanup;

public class SyntaxTreeHasher extends SHA256Hasher {

    protected void update(Node node) {
        @Cleanup TreeCursor cursor = node.walk();
        cursor.preorderTraversal(current -> {
            boolean leafNode = current.getChildCount() == 0;
            boolean isComment = current.getType().contains("comment");
            if (leafNode && !isComment) {
                String type = current.getType();
                byte[] bytes = type.getBytes();
                md.update(bytes);
            }
        });
    }
}
