package ch.usi.si.seart.transformer.wrapper;

import ch.usi.si.seart.transformer.JavaBaseTest;
import ch.usi.si.seart.transformer.Transformer;

class JavaDummyClassWrapperTest extends JavaBaseTest {

    @Override
    protected Transformer getTestSubject() {
        return new JavaDummyClassWrapper();
    }

    @Override
    protected String getTestInput() {
        return "public static void main(String... args) {\n    System.out.println(\"Hello, World!\");\n}";
    }

    @Override
    protected String getExpectedOutput() {
        return "class _ {\npublic static void main(String... args) {\n    System.out.println(\"Hello, World!\");\n}\n}";
    }
}
