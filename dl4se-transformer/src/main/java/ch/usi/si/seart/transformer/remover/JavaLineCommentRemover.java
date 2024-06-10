package ch.usi.si.seart.transformer.remover;

public class JavaLineCommentRemover extends JavaNodeRemover {

    @Override
    protected String getPattern() {
        return "(line_comment) @target";
    }
}
