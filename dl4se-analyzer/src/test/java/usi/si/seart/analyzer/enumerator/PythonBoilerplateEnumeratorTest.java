package usi.si.seart.analyzer.enumerator;

import lombok.Cleanup;
import lombok.SneakyThrows;
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

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

class PythonBoilerplateEnumeratorTest extends BaseTest {

    private static class PythonCodeProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of("def function():", null),
                    Arguments.of("def __new__():", Boilerplate.CONSTRUCTOR),
                    Arguments.of("def __init__():", Boilerplate.INITIALIZER),
                    Arguments.of("def __str__():", Boilerplate.STRING_CONVERSION),
                    Arguments.of("def __repr__():", Boilerplate.STRING_REPRESENTATION),
                    Arguments.of("def __hash__():", Boilerplate.HASHER),
                    Arguments.of("def __get__():", Boilerplate.GETTER),
                    Arguments.of("def __getattr__():", Boilerplate.GETTER),
                    Arguments.of("def __set__():", Boilerplate.SETTER),
                    Arguments.of("def __setattr__():", Boilerplate.SETTER),
                    Arguments.of("def __del__():", Boilerplate.FINALIZER),
                    Arguments.of("def __delattr__():", Boilerplate.FINALIZER),
                    Arguments.of("def __eq__():", Boilerplate.COMPARISON),
                    Arguments.of("def __lt__():", Boilerplate.COMPARISON),
                    Arguments.of("def __le__():", Boilerplate.COMPARISON),
                    Arguments.of("def __gt__():", Boilerplate.COMPARISON),
                    Arguments.of("def __ge__():", Boilerplate.COMPARISON),
                    Arguments.of("def __neg__():", Boilerplate.UNARY_ARITHMETIC),
                    Arguments.of("def __pos__():", Boilerplate.UNARY_ARITHMETIC),
                    Arguments.of("def __abs__():", Boilerplate.UNARY_ARITHMETIC),
                    Arguments.of("def __invert__():", Boilerplate.UNARY_ARITHMETIC),
                    Arguments.of("def __round__():", Boilerplate.UNARY_ARITHMETIC),
                    Arguments.of("def __floor__():", Boilerplate.UNARY_ARITHMETIC),
                    Arguments.of("def __ceil__():", Boilerplate.UNARY_ARITHMETIC),
                    Arguments.of("def __trunc__():", Boilerplate.UNARY_ARITHMETIC),
                    Arguments.of("def __add__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __sub__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __mul__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __div__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __truediv__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __floordiv__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __mod__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __pow__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __lshift__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __rshift__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __and__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __xor__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __or__():", Boilerplate.BINARY_ARITHMETIC),
                    Arguments.of("def __iadd__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __isub__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __imul__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __imatmul__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __itruediv__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __ifloordiv__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __imod__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __ipow__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __ilshift__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __irshift__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __iand__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __ixor__():", Boilerplate.AUGMENTED_ASSIGNMENT),
                    Arguments.of("def __ior__():", Boilerplate.AUGMENTED_ASSIGNMENT)
            );
        }
    }



    @ParameterizedTest(name = "[{index}] {1}")
    @ArgumentsSource(PythonCodeProvider.class)
    @SneakyThrows(UnsupportedEncodingException.class)
    void asEnumTest(String source, Boilerplate expected) {
        @Cleanup Parser parser = new Parser(Language.PYTHON);
        @Cleanup Tree tree = parser.parseString(source);
        Node root = tree.getRootNode();
        Node function_definition = root.getChild(0);
        BoilerplateEnumerator enumerator = new PythonBoilerplateEnumerator(
                () -> source.getBytes(StandardCharsets.UTF_16LE)
        );
        Boilerplate actual = enumerator.asEnum(function_definition);
        Assertions.assertEquals(expected, actual);
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
    @SneakyThrows(UnsupportedEncodingException.class)
    void asEnumTestSpecial(String source, Boilerplate expected) {
        @Cleanup Parser parser = new Parser(Language.PYTHON);
        @Cleanup Tree tree = parser.parseString(source);
        Node root = tree.getRootNode();
        Node decorated_definition = root.getChild(0);
        Node function_definition = decorated_definition.getChildByFieldName("definition");
        BoilerplateEnumerator enumerator = new PythonBoilerplateEnumerator(
                () -> source.getBytes(StandardCharsets.UTF_16LE)
        );
        Boilerplate actual = enumerator.asEnum(function_definition);
        Assertions.assertEquals(expected, actual);
    }
}