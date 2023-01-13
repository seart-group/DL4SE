package usi.si.seart.treesitter;

import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

class QueryCursorTest extends TestBase {

    @Test
    void testExecSimpleQuery() throws UnsupportedEncodingException {
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
    void testExecNoResultQuery() throws UnsupportedEncodingException {
        @Cleanup Parser parser = new Parser(Language.JAVA);
        @Cleanup Tree tree = parser.parseString("class Hello {}");
        @Cleanup Query query = new Query(Language.JAVA, "(method_declaration) @method");
        @Cleanup QueryCursor cursor = new QueryCursor();
        cursor.execQuery(query, tree.getRootNode());
        Assertions.assertNull(cursor.nextMatch());
    }
}
