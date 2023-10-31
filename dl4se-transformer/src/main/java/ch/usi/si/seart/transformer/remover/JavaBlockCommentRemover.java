package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.treesitter.Node;

import java.util.function.Predicate;

public class JavaBlockCommentRemover extends JavaNodeRemover {

    @Override
    protected String getPattern() {
        return "((block_comment) @capture (#match? @capture \"^/\\\\*[^*]\"))";
    }

    @Override
    protected Predicate<Node> getNodeFilter() {
        return node -> {
            String content = node.getContent();
            boolean isEmpty = content.equals("/**/");
            boolean isJavaDoc = content.startsWith("/**");
            return isEmpty || !isJavaDoc;
        };
    }
}
