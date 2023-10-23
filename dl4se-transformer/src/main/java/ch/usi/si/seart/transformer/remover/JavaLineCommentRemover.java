package ch.usi.si.seart.transformer.remover;

public class JavaLineCommentRemover extends JavaNodeRemover {

    @Override
    protected String getQueryPattern() {
        return "(line_comment) @target";
    }
}
