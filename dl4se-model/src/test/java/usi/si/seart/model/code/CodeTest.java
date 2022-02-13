package usi.si.seart.model.code;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class CodeTest {

    @Test
    void toStringTest() {
        List<Code> list = List.of(File.builder().build(), Function.builder().build());
        List<String> strings = list.stream().map(Code::toString).collect(Collectors.toList());
        Assertions.assertTrue(
                strings.get(0).startsWith("File")
        );
        Assertions.assertTrue(
                strings.get(1).startsWith("Function")
        );
    }
}