package ch.usi.si.seart.analyzer.util.stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

class DelimiterSuffixedStringCollectorTest {

    private static class StringsArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of("", Stream.of(), " "),
                    Arguments.of("A ", Stream.of("A"), " "),
                    Arguments.of("ABC", Stream.of("A", "B", "C"), ""),
                    Arguments.of("A B C ", Stream.of("A", "B", "C"), " ")
            );
        }
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @ArgumentsSource(StringsArgumentsProvider.class)
    void collectorTest(String expected, Stream<String> input, String delimiter) {
        Assertions.assertEquals(expected, input.collect(new DelimiterSuffixedStringCollector(delimiter)));
    }

    @Test
    void customEmptyTest() {
        String expected = "null";
        Stream<String> input = Stream.of();
        Assertions.assertEquals(expected, input.collect(new DelimiterSuffixedStringCollector(" ", expected)));
    }
}
