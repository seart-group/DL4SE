package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;

public class CharacterCounter extends TraverseCounter {

    @Override
    protected void nodeCallback(Node node) {
        boolean leafNode = node.getChildCount() == 0;
        if (leafNode) {
            String content = node.getContent();
            String noSpace = content.replaceAll("\\s", "");
            count.addAndGet(noSpace.length());
        }
    }
}
