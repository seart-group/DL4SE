package usi.si.seart.analyzer.hash;

import lombok.Cleanup;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.TreeCursor;

public class SyntaxTreeHasher extends SHA256Hasher {

    public SyntaxTreeHasher() {
        super();
    }

    @Override
    public String hash(Node node) {
        @Cleanup TreeCursor cursor = node.walk();
        cursor.preorderTraversal(current -> {
            boolean leafNode = current.getChildCount() == 0;
            boolean isComment = current.getType().contains("comment");
            if (leafNode && !isComment) {
                String type = current.getType();
                byte[] bytes = type.getBytes(DEFAULT_CHARSET);
                md.update(bytes);
            }
        });

        byte[] hash = md.digest();
        return bytesToHex(hash);
    }
}
