package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.treesitter.Node;

import java.util.function.Predicate;

public class JavaDocumentationCommentRemover extends JavaNodeRemover {

    @Override
    protected String getPattern() {
        return "((block_comment) @capture (#match? @capture \"^/\\\\*\\\\*\"))";
    }

    @Override
    protected Predicate<Node> getNodeFilter() {
        return node -> {
            String content = node.getContent();
            int length = content.length();
            return length > 4 && content.startsWith("/**");
        };
    }
}
