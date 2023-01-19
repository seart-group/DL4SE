package usi.si.seart.treesitter;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

class QueryCursorTest extends TestBase {

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void testExecSimpleQuery() {
        @Cleanup Parser parser = new Parser(Language.JAVA);
        @Cleanup Tree tree = parser.parseString("class Hello {}");
        @Cleanup Query query = new Query(Language.JAVA, "(class_body) @test");
        @Cleanup QueryCursor cursor = new QueryCursor();
        cursor.execQuery(query, tree.getRootNode());
        int numMatches = 0;
        while (cursor.nextMatch() != null) numMatches++;
        Assertions.assertEquals(1, numMatches, "Finds one match");
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void testExecNoResultQuery() {
        @Cleanup Parser parser = new Parser(Language.JAVA);
        @Cleanup Tree tree = parser.parseString("class Hello {}");
        @Cleanup Query query = new Query(Language.JAVA, "(method_declaration) @method");
        @Cleanup QueryCursor cursor = new QueryCursor();
        cursor.execQuery(query, tree.getRootNode());
        Assertions.assertNull(cursor.nextMatch());
    }
}
