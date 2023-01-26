package usi.si.seart.analyzer.predicate.node;

import usi.si.seart.analyzer.test.BaseTest;

public abstract class PredicateTest extends BaseTest {

    @Override
    protected String getInput() {
        return
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        // 软件工程的深度学习\n" +
            "        System.out.println(\"Hello, World!\");\n" +
            "    }\n" +
            "}";
    }
}
