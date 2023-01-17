package usi.si.seart.analyzer.predicate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

class ContainsNonAsciiPredicateTest extends PredicateTest {

    @Test
    void containsNonAsciiTest() {
        Predicate<Node> predicate = new ContainsNonAsciiPredicate(getNodeMapper());
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertTrue(result);
    }

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
        Predicate<Node> predicate = new ContainsNonAsciiPredicate(() -> bytes_2);
        Tree tree = parser.parseString(input_2);
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertFalse(result);
    }
}