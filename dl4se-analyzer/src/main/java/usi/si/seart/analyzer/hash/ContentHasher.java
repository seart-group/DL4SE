package usi.si.seart.analyzer.hash;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
import ch.usi.si.seart.treesitter.TreeCursor;
import lombok.Cleanup;
import lombok.Getter;
import usi.si.seart.analyzer.NodeMapper;

@Getter
public class ContentHasher extends SHA256Hasher {

    protected final NodeMapper mapper;

    public ContentHasher(NodeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected void update(Node node) {
        @Cleanup TreeCursor cursor = node.walk();
        cursor.preorderTraversal(current -> {
            boolean leafNode = current.getChildCount() == 0;
            boolean isComment = current.getType().contains("comment");
            if (leafNode && !isComment) {
                Range range = current.getRange();
                md.update(mapper.getBytesForRange(range));
            }
        });
    }
}
