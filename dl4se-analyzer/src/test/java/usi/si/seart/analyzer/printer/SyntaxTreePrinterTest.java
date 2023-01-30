package usi.si.seart.analyzer.printer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.JavaBaseTest;
import usi.si.seart.treesitter.Node;

import java.util.HashSet;

class SyntaxTreePrinterTest extends JavaBaseTest {

    @Test
    void printNothingTest() {
        Printer printer = new SyntaxTreePrinter();
        Assertions.assertEquals("", printer.print());
        Assertions.assertEquals("", printer.print(new HashSet<>()));
    }

    @Test
    void printRootTest() {
        Printer printer = new SyntaxTreePrinter();
        Node root = tree.getRootNode();
        String actual = printer.print(root);
        String expected =
                "program [0:0] - [7:1]\n" +
                "  package_declaration [0:0] - [0:18]\n" +
                "    scoped_identifier [0:8] - [0:17]\n" +
                "      scope: scoped_identifier [0:8] - [0:14]\n" +
                "        scope: identifier [0:8] - [0:10]\n" +
                "        name: identifier [0:11] - [0:14]\n" +
                "      name: identifier [0:15] - [0:17]\n" +
                "  class_declaration [2:0] - [7:1]\n" +
                "    modifiers [2:0] - [2:6]\n" +
                "    name: identifier [2:13] - [2:17]\n" +
                "    body: class_body [2:18] - [7:1]\n" +
                "      method_declaration [3:4] - [6:5]\n" +
                "        modifiers [3:4] - [3:17]\n" +
                "        type: void_type [3:18] - [3:22]\n" +
                "        name: identifier [3:23] - [3:27]\n" +
                "        parameters: formal_parameters [3:27] - [3:42]\n" +
                "          formal_parameter [3:28] - [3:41]\n" +
                "            type: array_type [3:28] - [3:36]\n" +
                "              element: type_identifier [3:28] - [3:34]\n" +
                "              dimensions: dimensions [3:34] - [3:36]\n" +
                "            name: identifier [3:37] - [3:41]\n" +
                "        body: block [3:43] - [6:5]\n" +
                "          line_comment [4:8] - [4:22]\n" +
                "          expression_statement [5:8] - [5:44]\n" +
                "            method_invocation [5:8] - [5:43]\n" +
                "              object: field_access [5:8] - [5:18]\n" +
                "                object: identifier [5:8] - [5:14]\n" +
                "                field: identifier [5:15] - [5:18]\n" +
                "              name: identifier [5:19] - [5:26]\n" +
                "              arguments: argument_list [5:26] - [5:43]\n" +
                "                string_literal [5:27] - [5:42]\n";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void printChildTest() {
        Printer printer = new SyntaxTreePrinter();
        Node root = tree.getRootNode();
        Node method = root.getChild(1).getChildByFieldName("body").getChild(1);
        String actual = printer.print(method);
        String expected =
                "method_declaration [3:4] - [6:5]\n" +
                "  modifiers [3:4] - [3:17]\n" +
                "  type: void_type [3:18] - [3:22]\n" +
                "  name: identifier [3:23] - [3:27]\n" +
                "  parameters: formal_parameters [3:27] - [3:42]\n" +
                "    formal_parameter [3:28] - [3:41]\n" +
                "      type: array_type [3:28] - [3:36]\n" +
                "        element: type_identifier [3:28] - [3:34]\n" +
                "        dimensions: dimensions [3:34] - [3:36]\n" +
                "      name: identifier [3:37] - [3:41]\n" +
                "  body: block [3:43] - [6:5]\n" +
                "    line_comment [4:8] - [4:22]\n" +
                "    expression_statement [5:8] - [5:44]\n" +
                "      method_invocation [5:8] - [5:43]\n" +
                "        object: field_access [5:8] - [5:18]\n" +
                "          object: identifier [5:8] - [5:14]\n" +
                "          field: identifier [5:15] - [5:18]\n" +
                "        name: identifier [5:19] - [5:26]\n" +
                "        arguments: argument_list [5:26] - [5:43]\n" +
                "          string_literal [5:27] - [5:42]\n";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void printMultipleTest() {
        Printer printer = new SyntaxTreePrinter();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        String actual = printer.print(package_declaration, class_declaration);
        String expected =
                "package_declaration [0:0] - [0:18]\n" +
                "  scoped_identifier [0:8] - [0:17]\n" +
                "    scope: scoped_identifier [0:8] - [0:14]\n" +
                "      scope: identifier [0:8] - [0:10]\n" +
                "      name: identifier [0:11] - [0:14]\n" +
                "    name: identifier [0:15] - [0:17]\n" +
                "class_declaration [2:0] - [7:1]\n" +
                "  modifiers [2:0] - [2:6]\n" +
                "  name: identifier [2:13] - [2:17]\n" +
                "  body: class_body [2:18] - [7:1]\n" +
                "    method_declaration [3:4] - [6:5]\n" +
                "      modifiers [3:4] - [3:17]\n" +
                "      type: void_type [3:18] - [3:22]\n" +
                "      name: identifier [3:23] - [3:27]\n" +
                "      parameters: formal_parameters [3:27] - [3:42]\n" +
                "        formal_parameter [3:28] - [3:41]\n" +
                "          type: array_type [3:28] - [3:36]\n" +
                "            element: type_identifier [3:28] - [3:34]\n" +
                "            dimensions: dimensions [3:34] - [3:36]\n" +
                "          name: identifier [3:37] - [3:41]\n" +
                "      body: block [3:43] - [6:5]\n" +
                "        line_comment [4:8] - [4:22]\n" +
                "        expression_statement [5:8] - [5:44]\n" +
                "          method_invocation [5:8] - [5:43]\n" +
                "            object: field_access [5:8] - [5:18]\n" +
                "              object: identifier [5:8] - [5:14]\n" +
                "              field: identifier [5:15] - [5:18]\n" +
                "            name: identifier [5:19] - [5:26]\n" +
                "            arguments: argument_list [5:26] - [5:43]\n" +
                "              string_literal [5:27] - [5:42]\n";
        Assertions.assertEquals(expected, actual);
    }
}