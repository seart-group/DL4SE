package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;

public class PythonCodeTokenCounter extends CodeTokenCounter {

    @Override
    protected void nodeCallback(Node node) {
        String type = node.getType();
        boolean leafNode = node.getChildCount() == 0;
        boolean isComment = type.equals("comment");
        boolean isString = type.equals("string");
        if (isString || (leafNode && !isComment))
            count.incrementAndGet();
    }

    @Override
    protected boolean shouldExplore(Node node) {
        String type = node.getType();
        return !type.equals("string");
    }
}
