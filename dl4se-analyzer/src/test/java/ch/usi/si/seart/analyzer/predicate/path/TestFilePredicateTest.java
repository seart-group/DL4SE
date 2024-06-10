package ch.usi.si.seart.analyzer.predicate.path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.stream.Stream;

class TestFilePredicateTest {

    private static class PathPredicateProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(Path.of("/src", "test", "README"), true),
                    Arguments.of(Path.of("/src", "tests", "README"), true),
                    Arguments.of(Path.of("/src", "latest", "README"), false),
                    Arguments.of(Path.of("/src", "main", "App.java"), false),
                    Arguments.of(Path.of("/src", "main", "Test.java"), false),
                    Arguments.of(Path.of("/src", "main", "TestApp.java"), false)
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PathPredicateProvider.class)
    void pathPredicateTest(Path path, boolean expected) {
        Predicate<Path> predicate = new TestFilePredicate() {};
        Assertions.assertEquals(expected, predicate.test(path));
    }
}
