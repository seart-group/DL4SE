package usi.si.seart.treesitter;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;

class QueryCursorTest extends TestBase {

    private static final String source = "class Hello {}";
    private static final Language language = Language.JAVA;
    private static Parser parser;
    private static Tree tree;
    private static Node root;

    @BeforeAll
    @SneakyThrows(UnsupportedEncodingException.class)
    static void beforeAll() {
        parser = new Parser(language);
        tree = parser.parseString(source);
        root = tree.getRootNode();
    }

    @AfterAll
    static void afterAll() {
        tree.close();
        parser.close();
    }

    @Test
    void testExecSimpleQuery() {
        @Cleanup Query query = new Query(language, "(class_body) @test");
        @Cleanup QueryCursor cursor = new QueryCursor();
        cursor.execQuery(query, root);
        int numMatches = 0;
        while (cursor.nextMatch() != null) numMatches++;
        Assertions.assertEquals(1, numMatches, "Must find one match!");
    }

    @Test
    void testExecNoResultQuery() {
        @Cleanup Query query = new Query(language, "(method_declaration) @method");
        @Cleanup QueryCursor cursor = new QueryCursor();
        cursor.execQuery(query, root);
        Assertions.assertNull(cursor.nextMatch(), "Must find no matches!");
    }
}
