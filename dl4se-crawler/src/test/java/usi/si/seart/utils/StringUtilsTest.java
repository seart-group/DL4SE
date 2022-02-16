package usi.si.seart.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void sha256Test() {
        // Expected generated from: https://passwordsgenerator.net/sha256-hash-generator/
        Assertions.assertEquals(
                "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
                StringUtils.sha256("Hello World!")
        );
    }

    @Test
    void containsNonAsciiTest() {
        String str1 = "Ovo je proba";
        String str2 = "Ово је проба";
        String str3 = "Ovo je проба";
        String str4 = "Ово је proba";
        Assertions.assertFalse(StringUtils.containsNonAscii(str1));
        Assertions.assertTrue(StringUtils.containsNonAscii(str2));
        Assertions.assertTrue(StringUtils.containsNonAscii(str3));
        Assertions.assertTrue(StringUtils.containsNonAscii(str4));
    }

    @Test
    void normalizeSpaceTest() {
        String input1 = "     ";
        String input2 = "This is a String";
        String input3 = " This is a String ";
        String input4 = "This    is     a      String";
        String input5 = "This\nis\ta\rString";
        String input6 = "This\n\n\nis       a\r\r\rString";
        Assertions.assertEquals("", StringUtils.normalizeSpace(input1));
        Assertions.assertEquals(input2, StringUtils.normalizeSpace(input2));
        Assertions.assertEquals(input2, StringUtils.normalizeSpace(input3));
        Assertions.assertEquals(input2, StringUtils.normalizeSpace(input4));
        Assertions.assertEquals(input2, StringUtils.normalizeSpace(input5));
        Assertions.assertEquals(input2, StringUtils.normalizeSpace(input6));
    }
}