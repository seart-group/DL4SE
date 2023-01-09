package usi.si.seart.analyzer.predicate;

import usi.si.seart.analyzer.test.BaseTest;

import java.nio.charset.StandardCharsets;

public abstract class PredicateTest extends BaseTest {

    protected final String input_1 =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "\tpublic static void main(String[] args) {\n" +
            "\t\t// 软件工程的深度学习\n" +
            "\t\tSystem.out.println(\"Hello, World!\");\n" +
            "\t}\n" +
            "}";

    protected final byte[] bytes_1 = input_1.getBytes(StandardCharsets.UTF_16LE);
}
