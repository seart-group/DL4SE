package usi.si.seart.analyzer.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.LibraryLoader;
import usi.si.seart.treesitter.Parser;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseTest {

    protected static Parser parser;

    static {
        LibraryLoader.load();
    }

    @BeforeAll
    static void beforeAll() {
        parser = new Parser(Language.JAVA);
    }

    @AfterAll
    static void afterAll() {
        parser.close();
    }

    protected Tree tree;

    @BeforeEach
    @SneakyThrows(UnsupportedEncodingException.class)
    void setUp() {
        tree = parser.parseString(input_1);
    }

    @AfterEach
    void tearDown() {
        tree.close();
        tree = null;
    }

    protected final String input_1 =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        //line comment\n" +
            "        System.out.println(\"Hello, World!\");\n" +
            "    }\n" +
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

    protected final String nodes_joined_1 = String.join("", nodes_1);

    protected final byte[] bytes_1 = input_1.getBytes(StandardCharsets.UTF_16LE);

    protected final String input_2 =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        /*\n" +
            "         * Block comment\n" +
            "         * on multiple lines\n" +
            "         */\n" +
            "        System.out.println(\"Hello, World!\");\n" +
            "    }\n" +
            "}";

    protected final byte[] bytes_2 = input_2.getBytes(StandardCharsets.UTF_16LE);
}
