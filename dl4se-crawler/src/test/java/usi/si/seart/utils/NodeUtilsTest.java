package usi.si.seart.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.collection.Tuple;

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
     * Non Space: 11
     */
    @Test
    void countTokens() {
        Tuple<Long, Long> tokens = NodeUtils.countTokens(compilationUnit);
        Assertions.assertEquals(17, tokens.getLeft());
        Assertions.assertEquals(11, tokens.getRight());
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

    @Test
    void getAstHash() {
        MethodDeclaration md1 = StaticJavaParser.parseMethodDeclaration("public void method(){ x += 1; }");
        MethodDeclaration md2 = StaticJavaParser.parseMethodDeclaration("public void method(){ a += 5; }");
        MethodDeclaration md3 = StaticJavaParser.parseMethodDeclaration("public void method(){ a += 5L; }");
        String hash1 = NodeUtils.getAstHash(md1);
        String hash2 = NodeUtils.getAstHash(md2);
        String hash3 = NodeUtils.getAstHash(md3);
        Assertions.assertEquals(hash1, hash2);
        Assertions.assertNotEquals(hash2, hash3);
    }
}