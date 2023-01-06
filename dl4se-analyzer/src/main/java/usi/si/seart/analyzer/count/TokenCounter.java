package usi.si.seart.analyzer.count;

import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Range;

public class TokenCounter extends ContentTraverseCounter {

    public TokenCounter() {
        super();
    }

    public TokenCounter(byte[] bytes) {
        super(bytes);
    }

    @Override
    protected void nodeCallback(Node node) {
        if (!isReady()) return;
        boolean leafNode = node.getChildCount() == 0;
        if (leafNode) {
            String[] tokens;
            Range range = node.getRange();
            String content = getContentForRange(range);
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
