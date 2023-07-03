package usi.si.seart.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StringUtilsTest {

    @Test
    void sha256Test() {
        // Expected generated from: https://passwordsgenerator.net/sha256-hash-generator/
        Assertions.assertEquals(
                "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
                StringUtils.sha256("Hello World!")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Ово је проба",
            "Ovo je проба",
            "Ово је proba"
    })
    void containsNonAsciiTest(String input) {
        Assertions.assertTrue(StringUtils.containsNonAscii(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "This is a String",
            " This is a String ",
            "This    is     a      String",
            "This\nis\ta\rString",
            "\n\r This\n\n\nis       a\r\r\rString \r\n"
    })
    void normalizeSpaceTest(String input) {
        String baseline = "This is a String";
        Assertions.assertEquals(baseline, StringUtils.normalizeSpace(input));
    }
}