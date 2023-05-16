package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Range;
import usi.si.seart.analyzer.NodeMapper;

public class CharacterCounter extends ContentTraverseCounter {

    public CharacterCounter(NodeMapper mapper) {
        super(mapper);
    }

    @Override
    protected void nodeCallback(Node node) {
        boolean leafNode = node.getChildCount() == 0;
        if (leafNode) {
            Range range = node.getRange();
            String content = mapper.getContentForRange(range);
            String noSpace = content.replaceAll("\\s", "");
            count.addAndGet(noSpace.length());
        }
    }
}
