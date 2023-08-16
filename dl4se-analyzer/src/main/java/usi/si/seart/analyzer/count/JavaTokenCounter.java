package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
import org.apache.commons.lang3.StringUtils;
import usi.si.seart.analyzer.NodeMapper;

public class JavaTokenCounter extends TokenCounter {

    public JavaTokenCounter(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    protected void nodeCallback(Node node) {
        boolean leafNode = node.getChildCount() == 0;
        if (leafNode) {
            String[] tokens;
            Range range = node.getRange();
            String content = mapper.getContentForRange(range);
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
