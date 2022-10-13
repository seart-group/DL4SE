package usi.si.seart.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import usi.si.seart.collection.Tuple;
import usi.si.seart.model.code.Boilerplate;

import java.util.stream.Stream;

class JavaParserTest {

    private static final Node noComment = StaticJavaParser.parseMethodDeclaration(
            "public void method(){\nint x = 1;\n}"
    );
    private static final Node lineComment = StaticJavaParser.parseMethodDeclaration(
            "public void method(){\n// This is a single line comment\n}"
    );
    private static final Node blockComment = StaticJavaParser.parseMethodDeclaration(
            "public void method(){\n/* This is a\n* multi line comment\n*/\n}"
    );
    private static final Node jdocComment = StaticJavaParser.parseMethodDeclaration(
            "/** * This is a java documentation comment */ public void method(){}"
    );

    private static final class CountTokensArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(noComment, 19, 12),
                    Arguments.of(lineComment, 24, 7),
                    Arguments.of(blockComment, 28, 7),
                    Arguments.of(jdocComment, 26, 7)
            );
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class BoilerplateTypeArgumentProvider implements ArgumentsProvider {

        MethodDeclaration md1 = StaticJavaParser.parseMethodDeclaration("public void method(){}");
        MethodDeclaration md2 = StaticJavaParser.parseMethodDeclaration("public void setX(){}");
        MethodDeclaration md3 = StaticJavaParser.parseMethodDeclaration("public void getX(){}");
        MethodDeclaration md4 = StaticJavaParser.parseMethodDeclaration("public void builder(){}");
        MethodDeclaration md5 = StaticJavaParser.parseMethodDeclaration("public void toString(){}");
        MethodDeclaration md6 = StaticJavaParser.parseMethodDeclaration("public void equals(){}");
        MethodDeclaration md7 = StaticJavaParser.parseMethodDeclaration("public void hashCode(){}");
        ConstructorDeclaration cd = new ConstructorDeclaration();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(md1, null),
                    Arguments.of(md2, Boilerplate.SETTER),
                    Arguments.of(md3, Boilerplate.GETTER),
                    Arguments.of(md4, Boilerplate.BUILDER),
                    Arguments.of(md5, Boilerplate.TO_STRING),
                    Arguments.of(md6, Boilerplate.EQUALS),
                    Arguments.of(md7, Boilerplate.HASH_CODE),
                    Arguments.of(cd, Boilerplate.CONSTRUCTOR)
            );
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static final class GetAstHashArgumentProvider implements ArgumentsProvider {

        MethodDeclaration md1 = StaticJavaParser.parseMethodDeclaration("public void method(){ x += 1; }");
        MethodDeclaration md2 = StaticJavaParser.parseMethodDeclaration("public void method(){ a += 5; }");
        MethodDeclaration md3 = StaticJavaParser.parseMethodDeclaration("public void method(){ a += 5L; }");

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(md1, true),
                    Arguments.of(md2, true),
                    Arguments.of(md3, false)
            );
        }
    }

    private static final class CountLinesArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(noComment, 3),
                    Arguments.of(lineComment, 3),
                    Arguments.of(blockComment, 5),
                    Arguments.of(jdocComment, 1)
            );
        }
    }

    private static final class RemoveAllCommentsArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(noComment, 0),
                    Arguments.of(lineComment, 1),
                    Arguments.of(blockComment, 1),
                    Arguments.of(jdocComment, 1)
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(CountTokensArgumentProvider.class)
    void countTokensTest(Node node, long expectedLeft, long expectedRight) {
        Tuple<Long, Long> tokens = JavaParser.countTokens(node);
        Assertions.assertEquals(expectedLeft, tokens.getLeft());
        Assertions.assertEquals(expectedRight, tokens.getRight());
    }

    @ParameterizedTest
    @ArgumentsSource(BoilerplateTypeArgumentProvider.class)
    void boilerplateTypeTest(CallableDeclaration<?> declaration, Boilerplate expected) {
        Assertions.assertEquals(expected, JavaParser.getBoilerplateType(declaration));
    }

    @ParameterizedTest
    @ArgumentsSource(GetAstHashArgumentProvider.class)
    void getAstHashTest(MethodDeclaration declaration, boolean expected) {
        String baseline = "6abae81a5835bb1bbf4a8b2ce105271327e397ec6d453227cf8fd6043a1f2621"; // manually calculated
        String result = JavaParser.getAstHash(declaration);
        Assertions.assertEquals(expected, baseline.equals(result));
    }

    @ParameterizedTest
    @ArgumentsSource(CountLinesArgumentProvider.class)
    void countLinesTest(Node node, long expected) {
        long actual = JavaParser.countLines(node);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ArgumentsSource(RemoveAllCommentsArgumentProvider.class)
    void removeAllCommentsTest(Node node, int expected) {
        Node removed = JavaParser.withoutComments(node);
        int actual = node.getAllContainedComments().size() - removed.getAllContainedComments().size();
        Assertions.assertEquals(expected, actual);
    }
}