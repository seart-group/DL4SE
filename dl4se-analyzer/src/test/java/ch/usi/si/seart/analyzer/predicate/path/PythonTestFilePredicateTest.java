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

class PythonTestFilePredicateTest {

    private static class PythonPathPredicateProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(Path.of("/src", "app.py"), false),
                    Arguments.of(Path.of("/src", "test", "app.py"), true),
                    Arguments.of(Path.of("/src", "tests", "app.py"), true),
                    Arguments.of(Path.of("/src", "latest", "app.py"), false),
                    Arguments.of(Path.of("/src", "test.py"), true),
                    Arguments.of(Path.of("/src", "test_app.py"), true),
                    Arguments.of(Path.of("/src", "app_test.py"), true),
                    Arguments.of(Path.of("/src", "latest.py"), false)
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PythonPathPredicateProvider.class)
    void pathPredicateTest(Path path, boolean expected) {
        Predicate<Path> predicate = new PythonTestFilePredicate();
        Assertions.assertEquals(expected, predicate.test(path));
    }
}
