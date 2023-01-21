package usi.si.seart.treesitter;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class QueryTest extends TestBase {

    @Test
    @SuppressWarnings("resource")
    void testQuery() {
        @Cleanup Query query = new Query(Language.JAVA, "(_)");
        Assertions.assertNotNull(query, "Query is not null");
        Assertions.assertThrows(NullPointerException.class, () -> new Query(null, null));
        Assertions.assertThrows(NullPointerException.class, () -> new Query(Language.JAVA, null));
        Assertions.assertThrows(NullPointerException.class, () -> new Query(null, "(_)"));
        Assertions.assertThrows(UnsatisfiedLinkError.class, () -> new Query(Language._INVALID_, "(_)"));
        Assertions.assertThrows(SymbolicExpressionException.class, () -> new Query(Language.JAVA, "()"));
    }
}
