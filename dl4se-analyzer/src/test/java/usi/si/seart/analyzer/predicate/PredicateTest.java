package usi.si.seart.analyzer.predicate;

import usi.si.seart.analyzer.test.BaseTest;

import java.nio.charset.StandardCharsets;

public abstract class PredicateTest extends BaseTest {

    protected final String input_1 =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        // 软件工程的深度学习\n" +
            "        System.out.println(\"Hello, World!\");\n" +
            "    }\n" +
            "}";

    protected final byte[] bytes_1 = input_1.getBytes(StandardCharsets.UTF_16LE);
}
