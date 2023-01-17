package usi.si.seart.analyzer.printer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;
import usi.si.seart.treesitter.Node;

class SExpressionPrinterTest extends BaseTest {

    @Test
    void printTest() {
        Printer printer = new SExpressionPrinter();
        Node root = tree.getRootNode();
        String actual = printer.print(root);
        String expected =
                "(program " +
                 "(package_declaration " +
                 "(scoped_identifier " +
                 "scope: (scoped_identifier " +
                 "scope: (identifier) " +
                 "name: (identifier)) " +
                 "name: (identifier))) " +
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
                 "dimensions: (dimensions)) " +
                 "name: (identifier))) " +
                 "body: (block " +
                 "(line_comment) " +
                 "(expression_statement " +
                 "(method_invocation " +
                 "object: (field_access " +
                 "object: (identifier) " +
                 "field: (identifier)) " +
                 "name: (identifier) " +
                 "arguments: (argument_list " +
                 "(string_literal)))))))))";
        Assertions.assertEquals(expected, actual);

        Node method = root.getChild(1).getChildByFieldName("body").getChild(1);
        actual = printer.print(method);
        expected =
                "(method_declaration " +
                "(modifiers) " +
                "type: (void_type) " +
                "name: (identifier) " +
                "parameters: (formal_parameters " +
                "(formal_parameter " +
                "type: (array_type " +
                "element: (type_identifier) " +
                "dimensions: (dimensions)) " +
                "name: (identifier))) " +
                "body: (block " +
                "(line_comment) " +
                "(expression_statement " +
                "(method_invocation " +
                "object: (field_access " +
                "object: (identifier) " +
                "field: (identifier)) " +
                "name: (identifier) " +
                "arguments: (argument_list " +
                "(string_literal))))))";
        Assertions.assertEquals(expected, actual);
    }
}