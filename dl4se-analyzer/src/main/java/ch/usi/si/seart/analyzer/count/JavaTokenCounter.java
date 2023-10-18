package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import org.apache.commons.lang3.StringUtils;

public class JavaTokenCounter extends TokenCounter {

    @Override
    protected void nodeCallback(Node node) {
        boolean leafNode = node.getChildCount() == 0;
        if (leafNode) {
            String[] tokens;
            String content = node.getContent();
            switch (node.getType()) {
                case "block_comment":
                    content = StringUtils.substringBetween(content, "/*", "*/");
                    if (content.startsWith("*"))
                        content = content.substring(1);
                    tokens = StringUtils.split(content);
                    count.addAndGet(tokens.length + 2L);
                    break;
                case "line_comment":
                    content = StringUtils.substringAfter(content, "//");
                    tokens = StringUtils.split(content);
                    count.addAndGet(tokens.length + 1L);
                    break;
                default:
                    count.addAndGet(1L);
                    break;
            }
        }
    }
}
