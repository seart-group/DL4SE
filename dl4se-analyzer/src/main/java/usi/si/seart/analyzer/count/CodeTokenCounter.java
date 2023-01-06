package usi.si.seart.analyzer.count;

import usi.si.seart.treesitter.Node;

public class CodeTokenCounter extends TraverseCounter {

    @Override
    protected void nodeCallback(Node node) {
        boolean leafNode = node.getChildCount() == 0;
        boolean isComment = node.getType().contains("comment");
        if (leafNode && !isComment) count.incrementAndGet();
    }
}
