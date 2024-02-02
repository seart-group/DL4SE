package ch.usi.si.seart.analyzer.enumerate;

import ch.usi.si.seart.analyzer.JavaBaseTest;
import ch.usi.si.seart.model.code.Boilerplate;
import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Parser;
import ch.usi.si.seart.treesitter.Tree;
import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

class JavaBoilerplateEnumeratorTest extends JavaBaseTest {

    private static class JavaCodeProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("public class _ { public void run() {} }", null),
                    Arguments.of("public class _ { public static void main(String[] args) {} }", null),
                    Arguments.of("public class _ { public Main() {} }", Boilerplate.CONSTRUCTOR),
                    Arguments.of("public class _ { public Main(int i) {} }", Boilerplate.CONSTRUCTOR),
                    Arguments.of("public class _ { public void getI() {} }", Boilerplate.GETTER),
                    Arguments.of("public class _ { public void getI(int i) {} }", Boilerplate.GETTER),
                    Arguments.of("public class _ { public void setI() {} }", Boilerplate.SETTER),
                    Arguments.of("public class _ { public void setI(int i) {} }", Boilerplate.SETTER),
                    Arguments.of("public class _ { public void compareTo() {} }", Boilerplate.COMPARISON),
                    Arguments.of("public class _ { public void equals() {} }", Boilerplate.COMPARISON),
                    Arguments.of("public class _ { public void hashCode() {} }", Boilerplate.HASHER),
                    Arguments.of("public class _ { public void toString() {} }", Boilerplate.STRING_CONVERSION),
                    Arguments.of("public class _ { public void writeObject() {} }", Boilerplate.SERIALIZER),
                    Arguments.of("public class _ { public void readObject() {} }", Boilerplate.DESERIALIZER),
                    Arguments.of("public class _ { public void readObjectNoData() {} }", Boilerplate.DESERIALIZER),
                    Arguments.of("public class _ { public void clone() {} }", Boilerplate.CLONER),
                    Arguments.of("public class _ { public void finalize() {} }", Boilerplate.FINALIZER)
            );
        }
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @ArgumentsSource(JavaCodeProvider.class)
    void asEnumTest(String source, Boilerplate expected) {
        BoilerplateEnumerator enumerator = new JavaBoilerplateEnumerator();
        @Cleanup Parser parser = Parser.getFor(Language.JAVA);
        @Cleanup Tree tree = parser.parse(source);
        Node root = tree.getRootNode();
        Node class_declaration = root.getChild(0);
        Node class_body = class_declaration.getChildByFieldName("body");
        Node callable_declaration = class_body.getChild(1);
        Boilerplate actual = enumerator.asEnum(callable_declaration);
        Assertions.assertEquals(expected, actual);
    }
}
