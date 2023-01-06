package usi.si.seart.analyzer.count;

import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Range;

public class CharacterCounter extends ContentTraverseCounter {

    public CharacterCounter() {
        super();
    }

    public CharacterCounter(byte[] bytes) {
        super(bytes);
    }

    @Override
    protected void nodeCallback(Node node) {
        if (!isReady()) return;
        boolean leafNode = node.getChildCount() == 0;
        if (leafNode) {
            Range range = node.getRange();
            String content = getContentForRange(range);
            String noSpace = content.replaceAll("\\s", "");
            count.addAndGet(noSpace.length());
        }
    }
}
