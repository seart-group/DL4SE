package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import org.apache.commons.lang3.StringUtils;

public class PythonTokenCounter extends TokenCounter {

    @Override
    protected void nodeCallback(Node node) {
        String type = node.getType();
        boolean leafNode = node.getChildCount() == 0;
        if (type.equals("string")) {
            count.addAndGet(1L);
        } else if (leafNode && type.equals("comment")) {
            String content = node.getContent();
            content = StringUtils.substringAfter(content, "#");
            String[] tokens = StringUtils.split(content);
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
