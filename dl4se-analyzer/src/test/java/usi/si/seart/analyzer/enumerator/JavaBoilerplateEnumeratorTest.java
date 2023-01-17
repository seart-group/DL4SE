package usi.si.seart.analyzer.enumerator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.treesitter.Node;

class JavaBoilerplateEnumeratorTest extends BaseTest {

    @Override
    protected String getInput() {
        return
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "    public Main() {}\n" +
            "    public Main(int i) {}\n" +
            "    public void getI() {}\n" +
            "    public void getI(int i) {}\n" +
            "    public void setI() {}\n" +
            "    public void setI(int i) {}\n" +
            "    public void builder() {}\n" +
            "    public void equals() {}\n" +
            "    public void hashCode() {}\n" +
            "    public void toString() {}\n" +
            "    public void run() {}\n" +
            "    public static void main(String[] args) {}\n" +
            "}";
    }

    @Test
    void asEnumTest() {
        BoilerplateEnumerator enumerator = new JavaBoilerplateEnumerator(getNodeMapper());
        Node root = tree.getRootNode();
        Node clazz = root.getChild(1);
        Node body = clazz.getChildByFieldName("body");
        Node constructor_0 = body.getChild(1);
        Node constructor_1 = body.getChild(2);
        Node method_0 = body.getChild(3);
        Node method_1 = body.getChild(4);
        Node method_2 = body.getChild(5);
        Node method_3 = body.getChild(6);
        Node method_4 = body.getChild(7);
        Node method_5 = body.getChild(8);
        Node method_6 = body.getChild(9);
        Node method_7 = body.getChild(10);
        Node method_8 = body.getChild(11);
        Node method_9 = body.getChild(12);
        Assertions.assertEquals(Boilerplate.CONSTRUCTOR, enumerator.asEnum(constructor_0));
        Assertions.assertEquals(Boilerplate.CONSTRUCTOR, enumerator.asEnum(constructor_1));
        Assertions.assertEquals(Boilerplate.GETTER, enumerator.asEnum(method_0));
        Assertions.assertEquals(Boilerplate.GETTER, enumerator.asEnum(method_1));
        Assertions.assertEquals(Boilerplate.SETTER, enumerator.asEnum(method_2));
        Assertions.assertEquals(Boilerplate.SETTER, enumerator.asEnum(method_3));
        Assertions.assertEquals(Boilerplate.BUILDER, enumerator.asEnum(method_4));
        Assertions.assertEquals(Boilerplate.EQUALS, enumerator.asEnum(method_5));
        Assertions.assertEquals(Boilerplate.HASH_CODE, enumerator.asEnum(method_6));
        Assertions.assertEquals(Boilerplate.TO_STRING, enumerator.asEnum(method_7));
        Assertions.assertNull(enumerator.asEnum(method_8));
        Assertions.assertNull(enumerator.asEnum(method_9));
    }
}