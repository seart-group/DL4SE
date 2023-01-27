package usi.si.seart.analyzer.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.LibraryLoader;
import usi.si.seart.treesitter.Parser;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseTest {

    static {
        LibraryLoader.load();
    }

    protected Parser parser;
    protected Tree tree;

    @BeforeEach
    protected void setUp() {
        setUpParser();
        setUpTree();
    }

    void setUpParser() {
        parser = new Parser(getLanguage());
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    void setUpTree() {
        tree = parser.parseString(getInput());
    }

    @AfterEach
    void tearDown() {
        parser.close();
        tree.close();
    }

    protected Language getLanguage() {
        return Language.JAVA;
    }

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

    protected byte[] getBytes() {
        return getInput().getBytes(StandardCharsets.UTF_16LE);
    }

    protected NodeMapper getNodeMapper() {
        return this::getBytes;
    }

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

    protected String getJoinedTokens() {
        return String.join("", getTokens());
    }

    protected List <String> getNodes() {
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

    protected String getJoinedNodes() {
        return String.join("", getNodes());
    }
}
