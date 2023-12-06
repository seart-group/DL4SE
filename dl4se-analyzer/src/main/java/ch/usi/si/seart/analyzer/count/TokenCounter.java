package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;

public abstract class TokenCounter extends TraverseCounter {

    @Override
    protected void nodeCallback(Node node) {
        boolean leafNode = node.getChildCount() == 0;
        if (leafNode) {
            String type = node.getType();
            if (type.equals("comment")) {
                String content = node.getContent();
                String[] tokens = content.split("\\s+");
                count.addAndGet(tokens.length);
            } else {
                count.addAndGet(1L);
            }
        }
    }

    public static TokenCounter getInstance(Language language) {
        switch (language) {
            case JAVA: return new JavaTokenCounter();
            case PYTHON: return new PythonTokenCounter();
            default: return new TokenCounter() {};
        }
    }
}
