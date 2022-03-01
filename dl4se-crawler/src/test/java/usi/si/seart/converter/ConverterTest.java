package usi.si.seart.converter;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ConverterTest {

    static Converter<Integer, String> intToStringConverter;
    static Converter<String, Integer> stringToIntConverter;

    private static class IntToStringConverter extends Converter<Integer, String> {
        @Override
        protected String forward(Integer i) {
            return i.toString();
        }

        @Override
        protected Integer backward(String s) {
            return Integer.parseInt(s);
        }
    }

    @BeforeAll
    public static void before() {
        intToStringConverter = new IntToStringConverter();
        stringToIntConverter = intToStringConverter.reverse();
    }

    @Test
    void singleConversionTest() {
        Integer i = 5;
        String s = "5";
        Assertions.assertEquals(s, intToStringConverter.convert(i));
        Assertions.assertEquals(i, stringToIntConverter.convert(s));
    }

    @Test
    void multipleConversionTest() {
        List<Integer> iList = List.of(1, 2, 3, 4);
        List<String> sList = List.of("1", "2", "3", "4");

        Spliterator<String> spliterator1 = intToStringConverter.convertAll(iList).spliterator();
        Spliterator<Integer> spliterator2 = stringToIntConverter.convertAll(sList).spliterator();

        List<String> iConverted = StreamSupport.stream(spliterator1, false).collect(Collectors.toList());
        List<Integer> sConverted = StreamSupport.stream(spliterator2, false).collect(Collectors.toList());

        Assertions.assertEquals(sList, iConverted);
        Assertions.assertEquals(iList, sConverted);
    }
}