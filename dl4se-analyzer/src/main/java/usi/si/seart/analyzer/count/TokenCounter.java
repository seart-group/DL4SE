package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
import usi.si.seart.analyzer.NodeMapper;

public abstract class TokenCounter extends ContentTraverseCounter {

    public TokenCounter(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    protected void nodeCallback(Node node) {
        boolean leafNode = node.getChildCount() == 0;
        if (leafNode) {
            String type = node.getType();
            if (type.equals("comment")) {
                Range range = node.getRange();
                String content = mapper.getContentForRange(range);
                String[] tokens = content.split("\\s+");
                count.addAndGet(tokens.length);
            } else {
                count.addAndGet(1L);
            }
        }
    }
}
