package ch.usi.si.seart.transformer.compressor;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;

public class JavaBlockCompressor extends NodeCompressor {

    @Override
    public Language getLanguage() {
        return Language.JAVA;
    }

    @Override
    protected String getPattern() {
        return "[" +
                    "(while_statement (block . (_) .) @block)" +
                    "(for_statement (block . (_) .) @block)" +
                    "(if_statement (block . (_) .) @block)" +
                    "(do_statement (block . (_) .) @block)" +
                "]";
    }

    @Override
    protected byte[] getTargetBytes(Node target) {
        Node child = target.getChild(1);
        String content = child.getContent();
        return content.getBytes();
    }
}
