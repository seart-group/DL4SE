package usi.si.seart.analyzer.printer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.analyzer.test.BaseTest;
import usi.si.seart.treesitter.Node;

class NodePrinterTest extends BaseTest {

    private final NodeMapper mapper = () -> bytes_1;

    @Test
    void printTest() {
        Printer printer = new NodePrinter(mapper);
        Node root = tree.getRootNode();
        String actual = printer.print(root);
        String expected = input_1;
        Assertions.assertEquals(expected, actual);

        Node method = root.getChild(1).getChildByFieldName("body").getChild(1);
        actual = printer.print(method);
        expected =
                "public static void main(String[] args) {\n" +
                "\t//line comment\n" +
                "\tSystem.out.println(\"Hello, World!\");\n" +
                "}";
        Assertions.assertEquals(expected, actual);
    }
}