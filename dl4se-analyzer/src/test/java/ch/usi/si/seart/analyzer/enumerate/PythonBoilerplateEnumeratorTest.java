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

import java.util.List;
import java.util.stream.Stream;

class PythonBoilerplateEnumeratorTest extends JavaBaseTest {

    private static class PythonCodeProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(List.of("def function():"), null),
                    Arguments.of(List.of("def __new__():"), Boilerplate.CONSTRUCTOR),
                    Arguments.of(List.of("def __init__():"), Boilerplate.INITIALIZER),
                    Arguments.of(List.of("def __str__():"), Boilerplate.STRING_CONVERSION),
                    Arguments.of(List.of("def __repr__():"), Boilerplate.STRING_REPRESENTATION),
                    Arguments.of(List.of("def __hash__():"), Boilerplate.HASHER),
                    Arguments.of(
                            List.of(
                                    "def __get__():",
                                    "def __getattr__():"
                            ),
                            Boilerplate.GETTER
                    ),
                    Arguments.of(
                            List.of(
                                    "def __set__():",
                                    "def __setattr__():"
                            ),
                            Boilerplate.SETTER
                    ),
                    Arguments.of(
                            List.of(
                                    "def __del__():",
                                    "def __delattr__():"
                            ),
                            Boilerplate.FINALIZER
                    ),
                    Arguments.of(
                            List.of(
                                    "def __eq__():",
                                    "def __lt__():",
                                    "def __le__():",
                                    "def __gt__():",
                                    "def __ge__():"
                            ),
                            Boilerplate.COMPARISON
                    ),
                    Arguments.of(
                            List.of(
                                    "def __neg__():",
                                    "def __pos__():",
                                    "def __abs__():",
                                    "def __invert__():",
                                    "def __round__():",
                                    "def __floor__():",
                                    "def __ceil__():",
                                    "def __trunc__():"
                            ),
                            Boilerplate.UNARY_ARITHMETIC
                    ),
                    Arguments.of(
                            List.of(
                                    "def __add__():",
                                    "def __sub__():",
                                    "def __mul__():",
                                    "def __div__():",
                                    "def __truediv__():",
                                    "def __floordiv__():",
                                    "def __mod__():",
                                    "def __pow__():",
                                    "def __lshift__():",
                                    "def __rshift__():",
                                    "def __and__():",
                                    "def __xor__():",
                                    "def __or__():"
                            ),
                            Boilerplate.BINARY_ARITHMETIC
                    ),
                    Arguments.of(
                            List.of(
                                    "def __iadd__():",
                                    "def __isub__():",
                                    "def __imul__():",
                                    "def __imatmul__():",
                                    "def __itruediv__():",
                                    "def __ifloordiv__():",
                                    "def __imod__():",
                                    "def __ipow__():",
                                    "def __ilshift__():",
                                    "def __irshift__():",
                                    "def __iand__():",
                                    "def __ixor__():",
                                    "def __ior__():"
                            ),
                            Boilerplate.AUGMENTED_ASSIGNMENT
                    )
            );
        }
    }



    @ParameterizedTest(name = "[{index}] {1}")
    @ArgumentsSource(PythonCodeProvider.class)
    void asEnumTest(List<String> sources, Boilerplate expected) {
        @Cleanup Parser parser = Parser.getFor(Language.PYTHON);
        for (String source: sources) {
            @Cleanup Tree tree = parser.parse(source);
            Node root = tree.getRootNode();
            Node function_definition = root.getChild(0);
            BoilerplateEnumerator enumerator = new PythonBoilerplateEnumerator();
            Boilerplate actual = enumerator.asEnum(function_definition);
            Assertions.assertEquals(expected, actual);
        }
    }

    private static class PythonCodeProviderSpecial implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("@something\ndef attr(self):\n    pass", null),
                    Arguments.of("@property\ndef attr(self):\n    pass", Boilerplate.GETTER),
                    Arguments.of("@property\n# line comment\ndef attr(self):\n    pass", Boilerplate.GETTER),
                    Arguments.of("@attr.setter\ndef attr(self, attr):\n    pass", Boilerplate.SETTER)
            );
        }
    }

    @ParameterizedTest(name = "[{index}] {1}")
    @ArgumentsSource(PythonCodeProviderSpecial.class)
    void asEnumTestSpecial(String source, Boilerplate expected) {
        @Cleanup Parser parser = Parser.getFor(Language.PYTHON);
        @Cleanup Tree tree = parser.parse(source);
        Node root = tree.getRootNode();
        Node decorated_definition = root.getChild(0);
        Node function_definition = decorated_definition.getChildByFieldName("definition");
        BoilerplateEnumerator enumerator = new PythonBoilerplateEnumerator();
        Boilerplate actual = enumerator.asEnum(function_definition);
        Assertions.assertEquals(expected, actual);
    }
}
