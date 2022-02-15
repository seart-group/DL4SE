package usi.si.seart.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
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

    @Test
    void isBoilerplate() {
        MethodDeclaration md1 = StaticJavaParser.parseMethodDeclaration("public void method(){}");
        MethodDeclaration md2 = StaticJavaParser.parseMethodDeclaration("public void setX(){}");
        MethodDeclaration md3 = StaticJavaParser.parseMethodDeclaration("public void getX(){}");
        MethodDeclaration md4 = StaticJavaParser.parseMethodDeclaration("public void builder(){}");
        MethodDeclaration md5 = StaticJavaParser.parseMethodDeclaration("public void build(){}");
        ConstructorDeclaration cd = new ConstructorDeclaration();
        Assertions.assertFalse(NodeUtils.isBoilerplate(md1));
        Assertions.assertTrue(NodeUtils.isBoilerplate(md2));
        Assertions.assertTrue(NodeUtils.isBoilerplate(md3));
        Assertions.assertTrue(NodeUtils.isBoilerplate(md4));
        Assertions.assertTrue(NodeUtils.isBoilerplate(md5));
        Assertions.assertTrue(NodeUtils.isBoilerplate(cd));
    }
}