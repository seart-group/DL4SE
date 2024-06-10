package ch.usi.si.seart.analyzer.predicate.node;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class ContainsNonAsciiPredicateTest extends PredicateTest {

    @Test
    void emptyInputTest() {
        NodePredicate predicate = new ContainsNonAsciiPredicate();
        Assertions.assertFalse(predicate.test());
        Assertions.assertFalse(predicate.test(new HashSet<>()));
    }

    @Test
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
        NodePredicate predicate = new ContainsNonAsciiPredicate();
        Tree tree = parser.parse(input_2);
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertFalse(result);
    }

    @Test
    void containsNonAsciiTest() {
        NodePredicate predicate = new ContainsNonAsciiPredicate();
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertTrue(result);
    }

    @Test
    void anyContainsNonAscii() {
        NodePredicate predicate = new ContainsNonAsciiPredicate();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        boolean result = predicate.test(package_declaration, class_declaration);
        Assertions.assertTrue(result);
    }
}
