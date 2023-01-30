package usi.si.seart.analyzer.traverser;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.JavaBaseTest;
import usi.si.seart.treesitter.Node;

import java.util.Collection;

class PreviousCommentTraverserTest extends JavaBaseTest {

    @Override
    protected String getInput() {
        return
            "/**\n" +
            " * A simple example class that contains a single field and two methods\n" +
            " */\n" +
            "public class Example {\n" +
            "\n" +
            "    private int value;\n" +
            "    \n" +
            "    /**\n" +
            "     * Constructor that initializes the value field\n" +
            "     * @param val the initial value\n" +
            "     */\n" +
            "    public Example(int val) {\n" +
            "        this.value = val;\n" +
            "    }\n" +
            "    \n" +
            "    /**\n" +
            "     * This method updates the value of the field with the new value\n" +
            "     * passed as an argument\n" +
            "     */\n" +
            "    // Returns the current value\n" +
            "    public int getValue() {\n" +
            "        return value;\n" +
            "    }\n" +
            "    \n" +
            "    /**\n" +
            "     * Method to update the value field\n" +
            "     */\n" +
            "    /*\n" +
            "     * This method updates the value of the field with the new value\n" +
            "     * passed as an argument\n" +
            "     */\n" +
            "    public void setValue(int newVal) {\n" +
            "        this.value = newVal;\n" +
            "    }\n" +
            "}";
    }

    @Test
    @SneakyThrows
    void getNodesTest() {
        Node root = tree.getRootNode();
        Node body = root.getChild(1).getChildByFieldName("body");
        Traverser<?> traverser = new PreviousCommentTraverser();

        Node target = body.getChild(3); // constructor
        Collection<Node> actual = traverser.getNodes(target);
        Assertions.assertEquals(1, actual.size());

        target = body.getChild(6); // getter
        actual = traverser.getNodes(target);
        Assertions.assertEquals(2, actual.size());

        target = body.getChild(9); // setter
        actual = traverser.getNodes(target);
        Assertions.assertEquals(2, actual.size());
    }
}