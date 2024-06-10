package ch.usi.si.seart.analyzer.predicate.node;

import ch.usi.si.seart.analyzer.JavaBaseTest;

public abstract class PredicateTest extends JavaBaseTest {

    @Override
    protected String getInput() {
        return
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        // 软件工程的深度学习\n" +
            "        System.out.println(\"Hello, World!\");\n" +
            "    }\n" +
            "}";
    }
}
