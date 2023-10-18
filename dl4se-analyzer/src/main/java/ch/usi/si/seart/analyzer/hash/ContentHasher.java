package ch.usi.si.seart.analyzer.hash;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.TreeCursor;
import lombok.Cleanup;
import lombok.Getter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Getter
public class ContentHasher extends SHA256Hasher {

    // FIXME: 21.09.23 This is so we retain consistency with the existing data
    private static final Charset CHARSET = StandardCharsets.UTF_16LE;

    @Override
    protected void update(Node node) {
        @Cleanup TreeCursor cursor = node.walk();
        cursor.preorderTraversal(current -> {
            boolean leafNode = current.getChildCount() == 0;
            boolean isComment = current.getType().contains("comment");
            if (leafNode && !isComment) {
                String content = current.getContent();
                byte[] bytes = content.getBytes(CHARSET);
                md.update(bytes);
            }
        });
    }
}
