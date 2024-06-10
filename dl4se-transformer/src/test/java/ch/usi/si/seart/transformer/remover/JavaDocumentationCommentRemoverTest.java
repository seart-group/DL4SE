package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.transformer.JavaBaseTest;
import ch.usi.si.seart.transformer.Transformer;

class JavaDocumentationCommentRemoverTest extends JavaBaseTest {

    @Override
    protected Transformer getTestSubject() {
        return new JavaDocumentationCommentRemover();
    }

    @Override
    protected String getTestInput() {
        return "package ch.usi.si;\n" +
                "\n" +
                "/**\n" +
                " * Class JavaDoc\n" +
                " */\n" +
                "public class Main {\n" +
                "\n" +
                "    /**\n" +
                "     * Method JavaDoc\n" +
                "     *//**/\n" +
                "    public static void main(String[] args /* comment */) {\n" +
                "        System.out.println(\"Hello, World!\"); /* comment */\n" +
                "        /* comment */\n" +
                "    }\n" +
                "}";
    }

    @Override
    protected String getExpectedOutput() {
        return "package ch.usi.si;\n" +
                "\n" +
                "\n" +
                "public class Main {\n" +
                "\n" +
                "    /**/\n" +
                "    public static void main(String[] args /* comment */) {\n" +
                "        System.out.println(\"Hello, World!\"); /* comment */\n" +
                "        /* comment */\n" +
                "    }\n" +
                "}";
    }
}
