package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
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
            String type = node.getType();
            switch (type) {
                case "block_comment":
                    int idxLo = (content.startsWith("/**")) ? 3 : 2;
                    int idxHi = content.length() - 2;
                    content = content.substring(idxLo, idxHi);
                    tokens = content.split("\\s+");
                    count.addAndGet(tokens.length + 2L);
                    break;
                case "line_comment":
                    content = content.substring(2);
                    tokens = content.split("\\s+");
                    count.addAndGet(tokens.length + 1L);
                    break;
                default:
                    count.addAndGet(1L);
                    break;
            }
        }
    }
}
