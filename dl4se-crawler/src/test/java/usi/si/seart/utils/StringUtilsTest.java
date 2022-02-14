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
}