package usi.si.seart.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}