package usi.si.seart.analyzer.enumerator;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import usi.si.seart.analyzer.test.BaseTest;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Parser;
import usi.si.seart.treesitter.Tree;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

class JavaBoilerplateEnumeratorTest extends BaseTest {

    private static final String input_static =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "    public Main() {}\n" +
            "    public Main(int i) {}\n" +
            "    public void getI() {}\n" +
            "    public void getI(int i) {}\n" +
            "    public void setI() {}\n" +
            "    public void setI(int i) {}\n" +
            "    public void builder() {}\n" +
            "    public void equals() {}\n" +
            "    public void hashCode() {}\n" +
            "    public void toString() {}\n" +
            "    public void run() {}\n" +
            "    public static void main(String[] args) {}\n" +
            "}";

    protected final String input_1 = input_static;
    protected final byte[] bytes_1 = input_1.getBytes(StandardCharsets.UTF_16LE);

    private static class JavaBoilerplateMethodProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            @Cleanup Parser parser = new Parser(Language.JAVA);
            Tree tree = parser.parseString(input_static);
            Node root = tree.getRootNode();
            Node clazz = root.getChild(1);
            Node body = clazz.getChildByFieldName("body");
            Node constructor_0 = body.getChild(1);
            Node constructor_1 = body.getChild(2);
            Node method_0 = body.getChild(3);
            Node method_1 = body.getChild(4);
            Node method_2 = body.getChild(5);
            Node method_3 = body.getChild(6);
            Node method_4 = body.getChild(7);
            Node method_5 = body.getChild(8);
            Node method_6 = body.getChild(9);
            Node method_7 = body.getChild(10);
            Node method_8 = body.getChild(11);
            Node method_9 = body.getChild(12);
            return Stream.of(
                    Arguments.of(constructor_0, Boilerplate.CONSTRUCTOR),
                    Arguments.of(constructor_1, Boilerplate.CONSTRUCTOR),
                    Arguments.of(method_0, Boilerplate.GETTER),
                    Arguments.of(method_1, Boilerplate.GETTER),
                    Arguments.of(method_2, Boilerplate.SETTER),
                    Arguments.of(method_3, Boilerplate.SETTER),
                    Arguments.of(method_4, Boilerplate.BUILDER),
                    Arguments.of(method_5, Boilerplate.EQUALS),
                    Arguments.of(method_6, Boilerplate.HASH_CODE),
                    Arguments.of(method_7, Boilerplate.TO_STRING),
                    Arguments.of(method_8, null),
                    Arguments.of(method_9, null)
            );
        }
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @ArgumentsSource(JavaBoilerplateMethodProvider.class)
    void boilerplateDetectionTest(Node node, Boilerplate expected) {
        BoilerplateEnumerator enumerator = new JavaBoilerplateEnumerator(() -> bytes_1);
        Assertions.assertEquals(expected, enumerator.asEnum(node));
    }
}