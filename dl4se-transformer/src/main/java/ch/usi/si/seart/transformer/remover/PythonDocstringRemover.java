package ch.usi.si.seart.transformer.remover;

public class PythonDocstringRemover extends PythonNodeRemover {

    @Override
    protected String getPattern() {
        return "[" +
                    "(module (comment)? . (expression_statement (string) @comment))" +
                    "(class_definition body: (block (comment)? . (expression_statement (string) @comment)))" +
                    "(function_definition body: (block (comment)? . (expression_statement (string) @comment)))" +
                "]";
    }
}
