package ch.usi.si.seart.transformer.remover;

public class PythonLineCommentRemover extends PythonNodeRemover {

    @Override
    protected String getQueryPattern() {
        return "(_ (comment) @target)";
    }
}
