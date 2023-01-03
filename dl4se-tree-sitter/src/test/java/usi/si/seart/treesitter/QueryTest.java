package usi.si.seart.treesitter;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class QueryTest extends TestBase {

    @Test
    void testQuery() {
        @Cleanup Query query = new Query(Language.JAVA, "(class_declaration)");
        Assertions.assertNotEquals(0, query.getPointer(), "Pointer is not null");
    }

    @Test
    void testInvalidQuery() {
        @Cleanup Query query = new Query(Language.JAVA, "(class_declaration");
        Assertions.assertEquals(0, query.getPointer(), "Pointer is null");
    }
}
