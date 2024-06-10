package ch.usi.si.seart.transformer;

import ch.usi.si.seart.transformer.remover.JavaDocumentationCommentRemover;
import ch.usi.si.seart.transformer.remover.JavaLineCommentRemover;
import ch.usi.si.seart.transformer.wrapper.JavaDummyClassWrapper;

public class TransformerChainTest extends BaseTest {

    @Override
    protected Transformer getTestSubject() {
        return Transformer.chain(
                new JavaDummyClassWrapper(),
                new JavaLineCommentRemover(),
                new JavaDocumentationCommentRemover()
        );
    }

    @Override
    protected String getTestInput() {
        return
                "/**\n" +
                " * Method JavaDoc\n" +
                " *//**/// line comment\n" +
                "public static void main(String[] args /* comment */) {\n" +
                "    System.out.println(\"Hello, World!\"); /* comment */\n" +
                "    /* comment */\n" +
                "}\n" +
                "// line comment\n";
    }

    @Override
    protected String getExpectedOutput() {
        return
                "class _ {\n" +
                "/**/\n" +
                "public static void main(String[] args /* comment */) {\n" +
                "    System.out.println(\"Hello, World!\"); /* comment */\n" +
                "    /* comment */\n" +
                "}\n" +
                "\n" +
                "\n" +
                "}";
    }
}
