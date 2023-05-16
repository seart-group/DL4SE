package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.JavaBaseTest;

import java.util.HashSet;

class SymbolicExpressionPrinterTest extends JavaBaseTest {

    @Test
    void printNothingTest() {
        Printer printer = new SymbolicExpressionPrinter();
        Assertions.assertEquals("", printer.print());
        Assertions.assertEquals("", printer.print(new HashSet<>()));
    }

    @Test
    void printRootTest() {
        Printer printer = new SymbolicExpressionPrinter();
        Node root = tree.getRootNode();
        String actual = printer.print(root);
        String expected =
        "(program " +
            "(package_declaration " +
                "(scoped_identifier " +
                    "scope: (scoped_identifier " +
                        "scope: (identifier) " +
                        "name: (identifier)" +
                    ") " +
                    "name: (identifier)" +
                ")" +
            ") " +
            "(class_declaration " +
                "(modifiers) " +
                "name: (identifier) " +
                "body: (class_body " +
                    "(method_declaration " +
                        "(modifiers) " +
                        "type: (void_type) " +
                        "name: (identifier) " +
                        "parameters: (formal_parameters " +
                            "(formal_parameter " +
                                "type: (array_type " +
                                    "element: (type_identifier) " +
                                    "dimensions: (dimensions)" +
                                ") " +
                                "name: (identifier)" +
                            ")" +
                        ") " +
                        "body: (block " +
                            "(line_comment) " +
                            "(expression_statement " +
                                "(method_invocation " +
                                    "object: (field_access " +
                                        "object: (identifier) " +
                                        "field: (identifier)" +
                                    ") " +
                                    "name: (identifier) " +
                                    "arguments: (argument_list " +
                                        "(string_literal)" +
                                    ")" +
                                ")" +
                            ")" +
                        ")" +
                    ")" +
                ")" +
            ")" +
        ")";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void printChildTest() {
        Printer printer = new SymbolicExpressionPrinter();
        Node root = tree.getRootNode();
        Node method = root.getChild(1).getChildByFieldName("body").getChild(1);
        String actual = printer.print(method);
        String expected =
        "(method_declaration " +
            "(modifiers) " +
            "type: (void_type) " +
            "name: (identifier) " +
            "parameters: (formal_parameters " +
                "(formal_parameter " +
                    "type: (array_type " +
                        "element: (type_identifier) " +
                        "dimensions: (dimensions)" +
                    ") " +
                    "name: (identifier)" +
                ")" +
            ") " +
            "body: (block " +
                "(line_comment) " +
                    "(expression_statement " +
                        "(method_invocation " +
                            "object: (field_access " +
                                "object: (identifier) " +
                                "field: (identifier)" +
                            ") " +
                        "name: (identifier) " +
                        "arguments: (argument_list " +
                            "(string_literal)" +
                        ")" +
                    ")" +
                ")" +
            ")" +
        ")";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void printMultipleTest() {
        Printer printer = new SymbolicExpressionPrinter();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        String actual = printer.print(package_declaration, class_declaration);
        // The "program" root node is removed because
        // we are printing its children individually
        String expected =
        "(" +
            "(package_declaration " +
                "(scoped_identifier " +
                    "scope: (scoped_identifier " +
                        "scope: (identifier) " +
                        "name: (identifier)" +
                    ") " +
                    "name: (identifier)" +
                ")" +
            ") " +
            "(class_declaration " +
                "(modifiers) " +
                "name: (identifier) " +
                "body: (class_body " +
                    "(method_declaration " +
                        "(modifiers) " +
                        "type: (void_type) " +
                        "name: (identifier) " +
                        "parameters: (formal_parameters " +
                            "(formal_parameter " +
                                "type: (array_type " +
                                    "element: (type_identifier) " +
                                    "dimensions: (dimensions)" +
                                ") " +
                                "name: (identifier)" +
                            ")" +
                        ") " +
                        "body: (block " +
                            "(line_comment) " +
                            "(expression_statement " +
                                "(method_invocation " +
                                    "object: (field_access " +
                                        "object: (identifier) " +
                                        "field: (identifier)" +
                                    ") " +
                                    "name: (identifier) " +
                                    "arguments: (argument_list " +
                                        "(string_literal)" +
                                    ")" +
                                ")" +
                            ")" +
                        ")" +
                    ")" +
                ")" +
            ")" +
        ")";
        Assertions.assertEquals(expected, actual);
    }
}