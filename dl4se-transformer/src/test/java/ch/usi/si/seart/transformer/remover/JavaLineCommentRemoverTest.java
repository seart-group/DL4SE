package ch.usi.si.seart.transformer.remover;

import ch.usi.si.seart.transformer.JavaBaseTest;
import ch.usi.si.seart.transformer.Transformer;

class JavaLineCommentRemoverTest extends JavaBaseTest {

    @Override
    protected Transformer getTestSubject() {
        return new JavaLineCommentRemover();
    }

    @Override
    protected String getTestInput() {
        return "package ch.usi.si;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        // preceding statement\n" +
                "        System.out.println(\"Hello, World!\"); // inline with statement\n" +
                "        // succeeding statement\n" +
                "    }\n" +
                "}";
    }

    @Override
    protected String getExpectedOutput() {
        return "package ch.usi.si;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        \n" +
                "        System.out.println(\"Hello, World!\"); \n" +
                "        \n" +
                "    }\n" +
                "}";
    }
}
