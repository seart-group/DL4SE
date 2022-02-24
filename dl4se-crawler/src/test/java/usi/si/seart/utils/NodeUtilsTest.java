package usi.si.seart.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.collection.Tuple;
import usi.si.seart.model.code.Boilerplate;

class NodeUtilsTest {

    CompilationUnit compilationUnit = StaticJavaParser.parse("class X { java.util.Y y; }");

    @Test
    void countTokens() {
        Tuple<Long, Long> tokens = NodeUtils.countTokens(compilationUnit);
        Assertions.assertEquals(17, tokens.getLeft());
        Assertions.assertEquals(11, tokens.getRight());

        MethodDeclaration md1 = StaticJavaParser.parseMethodDeclaration(
                "public void method(){\n// This is a single line comment\n}"
        );
        tokens = NodeUtils.countTokens(md1);
        Assertions.assertEquals(24, tokens.getLeft());
        Assertions.assertEquals(7, tokens.getRight());

        MethodDeclaration md2 = StaticJavaParser.parseMethodDeclaration(
                "public void method(){\n/* This is a\n* multi line comment\n*/\n}"
        );
        tokens = NodeUtils.countTokens(md2);
        Assertions.assertEquals(28, tokens.getLeft());
        Assertions.assertEquals(7, tokens.getRight());
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
        MethodDeclaration md5 = StaticJavaParser.parseMethodDeclaration("public void toString(){}");
        MethodDeclaration md6 = StaticJavaParser.parseMethodDeclaration("public void equals(){}");
        MethodDeclaration md7 = StaticJavaParser.parseMethodDeclaration("public void hashCode(){}");
        ConstructorDeclaration cd = new ConstructorDeclaration();
        Assertions.assertNull(NodeUtils.getBoilerplateType(md1));
        Assertions.assertEquals(Boilerplate.SETTER, NodeUtils.getBoilerplateType(md2));
        Assertions.assertEquals(Boilerplate.GETTER, NodeUtils.getBoilerplateType(md3));
        Assertions.assertEquals(Boilerplate.BUILDER, NodeUtils.getBoilerplateType(md4));
        Assertions.assertEquals(Boilerplate.TO_STRING, NodeUtils.getBoilerplateType(md5));
        Assertions.assertEquals(Boilerplate.EQUALS, NodeUtils.getBoilerplateType(md6));
        Assertions.assertEquals(Boilerplate.HASH_CODE, NodeUtils.getBoilerplateType(md7));
        Assertions.assertEquals(Boilerplate.CONSTRUCTOR, NodeUtils.getBoilerplateType(cd));
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