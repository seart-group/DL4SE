package usi.si.seart.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.collection.Tuple;
import usi.si.seart.model.code.Boilerplate;

class JavaParserTest {

    private final CompilationUnit compilationUnit = StaticJavaParser.parse("class X { java.util.Y y; }");

    @Test
    void countTokens() {
        Tuple<Long, Long> tokens = JavaParser.countTokens(compilationUnit);
        Assertions.assertEquals(17, tokens.getLeft());
        Assertions.assertEquals(11, tokens.getRight());

        MethodDeclaration md1 = StaticJavaParser.parseMethodDeclaration(
                "public void method(){\n// This is a single line comment\n}"
        );
        tokens = JavaParser.countTokens(md1);
        Assertions.assertEquals(24, tokens.getLeft());
        Assertions.assertEquals(7, tokens.getRight());

        MethodDeclaration md2 = StaticJavaParser.parseMethodDeclaration(
                "public void method(){\n/* This is a\n* multi line comment\n*/\n}"
        );
        tokens = JavaParser.countTokens(md2);
        Assertions.assertEquals(28, tokens.getLeft());
        Assertions.assertEquals(7, tokens.getRight());

        MethodDeclaration md3 = StaticJavaParser.parseMethodDeclaration(
                "/** * This is a java documentation comment */\npublic void method(){}"
        );
        tokens = JavaParser.countTokens(md3);
        Assertions.assertEquals(26, tokens.getLeft());
        Assertions.assertEquals(7, tokens.getRight());
    }

    @Test
    void getAstHash() {
        MethodDeclaration md1 = StaticJavaParser.parseMethodDeclaration("public void method(){ x += 1; }");
        MethodDeclaration md2 = StaticJavaParser.parseMethodDeclaration("public void method(){ a += 5; }");
        MethodDeclaration md3 = StaticJavaParser.parseMethodDeclaration("public void method(){ a += 5L; }");
        String hash1 = JavaParser.getAstHash(md1);
        String hash2 = JavaParser.getAstHash(md2);
        String hash3 = JavaParser.getAstHash(md3);
        Assertions.assertEquals(hash1, hash2);
        Assertions.assertNotEquals(hash2, hash3);
    }

    @Test
    void getBoilerplateTypeTest() {
        MethodDeclaration md1 = StaticJavaParser.parseMethodDeclaration("public void method(){}");
        MethodDeclaration md2 = StaticJavaParser.parseMethodDeclaration("public void setX(){}");
        MethodDeclaration md3 = StaticJavaParser.parseMethodDeclaration("public void getX(){}");
        MethodDeclaration md4 = StaticJavaParser.parseMethodDeclaration("public void builder(){}");
        MethodDeclaration md5 = StaticJavaParser.parseMethodDeclaration("public void toString(){}");
        MethodDeclaration md6 = StaticJavaParser.parseMethodDeclaration("public void equals(){}");
        MethodDeclaration md7 = StaticJavaParser.parseMethodDeclaration("public void hashCode(){}");
        ConstructorDeclaration cd = new ConstructorDeclaration();
        Assertions.assertNull(JavaParser.getBoilerplateType(md1));
        Assertions.assertEquals(Boilerplate.SETTER, JavaParser.getBoilerplateType(md2));
        Assertions.assertEquals(Boilerplate.GETTER, JavaParser.getBoilerplateType(md3));
        Assertions.assertEquals(Boilerplate.BUILDER, JavaParser.getBoilerplateType(md4));
        Assertions.assertEquals(Boilerplate.TO_STRING, JavaParser.getBoilerplateType(md5));
        Assertions.assertEquals(Boilerplate.EQUALS,JavaParser.getBoilerplateType(md6));
        Assertions.assertEquals(Boilerplate.HASH_CODE, JavaParser.getBoilerplateType(md7));
        Assertions.assertEquals(Boilerplate.CONSTRUCTOR, JavaParser.getBoilerplateType(cd));
    }

    @Test
    void countLinesTest() {
        long lines = JavaParser.countLines(compilationUnit);
        Assertions.assertEquals(1, lines);
    }
}