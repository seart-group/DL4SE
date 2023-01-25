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
            "    public void compareTo() {}\n" +
            "    public void equals() {}\n" +
            "    public void hashCode() {}\n" +
            "    public void toString() {}\n" +
            "    public void writeObject() {}\n" +
            "    public void readObject() {}\n" +
            "    public void readObjectNoData() {}\n" +
            "    public void clone() {}\n" +
            "    public void finalize() {}\n" +
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
        Node method_00 = body.getChild(3);
        Node method_01 = body.getChild(4);
        Node method_02 = body.getChild(5);
        Node method_03 = body.getChild(6);
        Node method_04 = body.getChild(7);
        Node method_05 = body.getChild(8);
        Node method_06 = body.getChild(9);
        Node method_07 = body.getChild(10);
        Node method_08 = body.getChild(11);
        Node method_09 = body.getChild(12);
        Node method_10 = body.getChild(13);
        Node method_11 = body.getChild(14);
        Node method_12 = body.getChild(15);
        Node method_13 = body.getChild(16);
        Node method_14 = body.getChild(17);

        Assertions.assertEquals(Boilerplate.CONSTRUCTOR, enumerator.asEnum(constructor_0));
        Assertions.assertEquals(Boilerplate.CONSTRUCTOR, enumerator.asEnum(constructor_1));
        Assertions.assertEquals(Boilerplate.GETTER, enumerator.asEnum(method_00));
        Assertions.assertEquals(Boilerplate.GETTER, enumerator.asEnum(method_01));
        Assertions.assertEquals(Boilerplate.SETTER, enumerator.asEnum(method_02));
        Assertions.assertEquals(Boilerplate.SETTER, enumerator.asEnum(method_03));
        Assertions.assertEquals(Boilerplate.COMPARISON, enumerator.asEnum(method_04));
        Assertions.assertEquals(Boilerplate.COMPARISON, enumerator.asEnum(method_05));
        Assertions.assertEquals(Boilerplate.HASHER, enumerator.asEnum(method_06));
        Assertions.assertEquals(Boilerplate.STRING_CONVERSION, enumerator.asEnum(method_07));
        Assertions.assertEquals(Boilerplate.SERIALIZER, enumerator.asEnum(method_08));
        Assertions.assertEquals(Boilerplate.DESERIALIZER, enumerator.asEnum(method_09));
        Assertions.assertEquals(Boilerplate.DESERIALIZER, enumerator.asEnum(method_10));
        Assertions.assertEquals(Boilerplate.CLONER, enumerator.asEnum(method_11));
        Assertions.assertEquals(Boilerplate.FINALIZER, enumerator.asEnum(method_12));
        Assertions.assertNull(enumerator.asEnum(method_13));
        Assertions.assertNull(enumerator.asEnum(method_14));
    }
}