package ch.usi.si.seart.io;

import ch.usi.si.seart.crawler.io.ExtensionBasedFileVisitor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("ConstantConditions")
class ExtensionBasedFileVisitorTest {

    private final Path resources = Path.of(this.getClass().getResource("/code").getPath());

    private static final class ExtensionsAndCountArgumentProvider implements ArgumentsProvider {

        /*
         * code:
         * - java: 8
         * - python: 4
         * - other: 4
         */
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new String[]{}, 16),
                    Arguments.of(new String[]{ "java", "py" }, 12),
                    Arguments.of(new String[]{ "java" }, 8)
            );
        }
    }

    private static final class InvalidExtensionsArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of((Object) new String[]{ "java", "py", null }),
                    Arguments.of((Object) new String[]{ "java", "py", "" }),
                    Arguments.of((Object) new String[]{ "java", "py", " " })
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ExtensionsAndCountArgumentProvider.class)
    @SneakyThrows(IOException.class)
    void visitorTest(String[] extensions, int expected) {
        ExtensionBasedFileVisitor visitor = ExtensionBasedFileVisitor.forExtensions(extensions);
        Files.walkFileTree(resources, visitor);
        List<Path> files = visitor.getVisited();
        Assertions.assertEquals(expected, files.size());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidExtensionsArgumentProvider.class)
    void invalidExtensionsTest(String[] extensions) {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> ExtensionBasedFileVisitor.forExtensions(extensions)
        );
    }

    @Test
    void nullExtensionsTest() {
        String[] invalid = null;
        Assertions.assertThrows(NullPointerException.class, () -> ExtensionBasedFileVisitor.forExtensions(invalid));
    }
}
