package usi.si.seart.analyzer.test;

import usi.si.seart.treesitter.Language;

import java.util.List;

public abstract class JavaBaseTest extends BaseTest {

    @Override
    protected Language getLanguage() {
        return Language.JAVA;
    }

    @Override
    protected String getInput() {
        return
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        //line comment\n" +
            "        System.out.println(\"Hello, World!\");\n" +
            "    }\n" +
            "}";
    }

    @Override
    protected List<String> getTokens() {
        return List.of(
            "package",
            "ch",
            ".",
            "usi",
            ".",
            "si",
            ";",
            "public",
            "class",
            "Main",
            "{",
            "//line comment",
            "public",
            "static",
            "void",
            "main",
            "(",
            "String",
            "[",
            "]",
            "args",
            ")",
            "{",
            "System",
            ".",
            "out",
            ".",
            "println",
            "(",
            "\"Hello, World!\"",
            ")",
            ";",
            "}",
            "}"
        );
    }

    @Override
    protected List<String> getNodes() {
        return List.of(
            "package",
            "identifier",
            ".",
            "identifier",
            ".",
            "identifier",
            ";",
            "public",
            "class",
            "identifier",
            "{",
            "line_comment",
            "public",
            "static",
            "void_type",
            "identifier",
            "(",
            "type_identifier",
            "[",
            "]",
            "identifier",
            ")",
            "{",
            "identifier",
            ".",
            "identifier",
            ".",
            "identifier",
            "(",
            "string_literal",
            ")",
            ";",
            "}",
            "}"
        );
    }
}
