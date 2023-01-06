package usi.si.seart.analyzer.test;

import usi.si.seart.treesitter.LibraryLoader;

import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseTest {

    static {
        LibraryLoader.load();
    }

    protected final String input_1 =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "\tpublic static void main(String[] args) {\n" +
            "\t\t//line comment\n" +
            "\t\tSystem.out.println(\"Hello, World!\");\n" +
            "\t}\n" +
            "}";
    
    protected final List<String> tokens_1 = List.of(
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
            "public",
            "static",
            "void",
            "main",
            "(",
            "String",
            "[]",
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

    protected final String tokens_joined_1 = String.join("", tokens_1);

    protected final List<String> nodes_1 = List.of(
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
            "public",
            "static",
            "void_type",
            "identifier",
            "(",
            "type_identifier",
            "[]",
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

    protected final String nodes_joined_1 = String.join("", nodes_1);

    protected final byte[] bytes_1 = input_1.getBytes(StandardCharsets.UTF_16LE);

    protected final String input_2 =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "\tpublic static void main(String[] args) {\n" +
            "\t\t/*\n" +
            "\t\t * Block comment\n" +
            "\t\t * on multiple lines\n" +
            "\t\t */\n" +
            "\t\tSystem.out.println(\"Hello, World!\");\n" +
            "\t}\n" +
            "}";

    protected final byte[] bytes_2 = input_2.getBytes(StandardCharsets.UTF_16LE);
}
