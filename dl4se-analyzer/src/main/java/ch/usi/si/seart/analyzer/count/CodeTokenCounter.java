package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;

public class CodeTokenCounter extends TraverseCounter {

    @Override
    protected void nodeCallback(Node node) {
        boolean leafNode = node.getChildCount() == 0;
        boolean isComment = node.getType().contains("comment");
        if (leafNode && !isComment) count.incrementAndGet();
    }

    public static CodeTokenCounter getInstance(Language language) {
        if (Language.PYTHON.equals(language))
            return new PythonCodeTokenCounter();
        return new CodeTokenCounter();
    }
}
