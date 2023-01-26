package usi.si.seart.analyzer.predicate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

class ContainsNonAsciiPredicateTest extends PredicateTest {

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void containsOnlyAsciiTest() {
        String input_2 =
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
        byte[] bytes_2 = input_2.getBytes(StandardCharsets.UTF_16LE);
        NodePredicate predicate = new ContainsNonAsciiPredicate(() -> bytes_2);
        Tree tree = parser.parseString(input_2);
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertFalse(result);
    }

    @Test
    void containsNonAsciiTest() {
        NodePredicate predicate = new ContainsNonAsciiPredicate(getNodeMapper());
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertTrue(result);
    }

    @Test
    void anyContainsNonAscii() {
        NodePredicate predicate = new ContainsNonAsciiPredicate(getNodeMapper());
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        boolean result = predicate.test(package_declaration, class_declaration);
        Assertions.assertTrue(result);
    }
}