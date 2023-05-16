package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.JavaBaseTest;

import java.util.HashSet;

class NodePrinterTest extends JavaBaseTest {

    @Test
    void printNothingTest() {
        Printer printer = new NodePrinter(getNodeMapper());
        Assertions.assertEquals("", printer.print());
        Assertions.assertEquals("", printer.print(new HashSet<>()));
    }

    @Test
    void printRootTest() {
        Printer printer = new NodePrinter(getNodeMapper());
        Node root = tree.getRootNode();
        String actual = printer.print(root);
        String expected = getInput();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void printChildTest() {
        Printer printer = new NodePrinter(getNodeMapper());
        Node root = tree.getRootNode();
        Node method = root.getChild(1).getChildByFieldName("body").getChild(1);
        String actual = printer.print(method);
        String expected =
                "public static void main(String[] args) {\n" +
                "    //line comment\n" +
                "    System.out.println(\"Hello, World!\");\n" +
                "}";
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void printMultipleTest() {
        Printer printer = new NodePrinter(getNodeMapper());
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        String actual = printer.print(package_declaration, class_declaration);
        // Extra newline is removed because information
        // about spaces between nodes is not maintained
        String expected =
                "package ch.usi.si;\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        //line comment\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";
        Assertions.assertEquals(expected, actual);
    }
}