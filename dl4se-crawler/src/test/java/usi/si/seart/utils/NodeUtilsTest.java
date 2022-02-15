package usi.si.seart.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NodeUtilsTest {

    CompilationUnit compilationUnit = StaticJavaParser.parse("class X { java.util.Y y; }");

    /*
     * Tokens:
     * - class
     * - [ ]
     * - X
     * - [ ]
     * - {
     * - [ ]
     * - java
     * - .
     * - util
     * - .
     * - Y
     * - [ ]
     * - y
     * - ;
     * - [ ]
     * - }
     * - [EOF]
     * Total: 17
     */
    @Test
    void countTokens() {
        long tokens = NodeUtils.countTokens(compilationUnit);
        Assertions.assertEquals(17, tokens);
    }

    @Test
    void countLines() {
        long lines = NodeUtils.countLines(compilationUnit);
        Assertions.assertEquals(1, lines);
    }
}