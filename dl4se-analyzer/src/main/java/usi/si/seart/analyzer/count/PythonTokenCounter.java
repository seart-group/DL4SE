package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
import usi.si.seart.analyzer.NodeMapper;

public class PythonTokenCounter extends TokenCounter {

    public PythonTokenCounter(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    protected void nodeCallback(Node node) {
        String type = node.getType();
        boolean leafNode = node.getChildCount() == 0;
        if (type.equals("string")) {
            count.addAndGet(1L);
        } else if (leafNode && type.equals("comment")) {
            Range range = node.getRange();
            String content = mapper.getContentForRange(range).substring(1);
            String[] tokens = content.split("\\s+");
            count.addAndGet(tokens.length + 1L);
        } else if (leafNode) {
            count.addAndGet(1L);
        }
    }

    @Override
    protected boolean shouldExplore(Node node) {
        String type = node.getType();
        return !type.equals("string");
    }
}
