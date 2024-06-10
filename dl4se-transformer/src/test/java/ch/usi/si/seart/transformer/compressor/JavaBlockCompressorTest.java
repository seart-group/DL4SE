package ch.usi.si.seart.transformer.compressor;

import ch.usi.si.seart.transformer.JavaBaseTest;
import ch.usi.si.seart.transformer.Transformer;

class JavaBlockCompressorTest extends JavaBaseTest {

    @Override
    protected Transformer getTestSubject() {
        return new JavaBlockCompressor();
    }

    @Override
    protected String getTestInput() {
        return "class Main {\n" +
                "    public static void main(String... args) {\n" +
                "        do {\n" +
                "            i++;\n" +
                "            i+=1;\n" +
                "        } while (i < 10);\n" +
                "\n" +
                "        do {\n" +
                "            i++;\n" +
                "        } while (i < 10);\n" +
                "\n" +
                "        do i++; while (i < 10);\n" +
                "\n" +
                "        while (i < 10) {\n" +
                "            i++;\n" +
                "            i+=1;\n" +
                "        }\n" +
                "\n" +
                "        while (i < 10) {\n" +
                "            i++;\n" +
                "        }\n" +
                "\n" +
                "        while (i < 10) i++;\n" +
                "\n" +
                "        for (int i = 0; i < 10; i++) {\n" +
                "            System.out.println(i);\n" +
                "            System.out.println(i);\n" +
                "        }\n" +
                "\n" +
                "        for (int i = 0; i < 10; i++) {\n" +
                "            System.out.println(i);\n" +
                "        }\n" +
                "\n" +
                "        for (int i = 0; i < 10; i++) System.out.println(i);\n" +
                "\n" +
                "        if (i < 10) {\n" +
                "            System.out.println(i);\n" +
                "            System.out.println(i);\n" +
                "        }\n" +
                "\n" +
                "        if (i < 10) {\n" +
                "            System.out.println(i);\n" +
                "        }\n" +
                "\n" +
                "        if (i < 10) System.out.println(i);\n" +
                "    }\n" +
                "}";
    }

    @Override
    protected String getExpectedOutput() {
        return "class Main {\n" +
                "    public static void main(String... args) {\n" +
                "        do {\n" +
                "            i++;\n" +
                "            i+=1;\n" +
                "        } while (i < 10);\n" +
                "\n" +
                "        do i++; while (i < 10);\n" +
                "\n" +
                "        do i++; while (i < 10);\n" +
                "\n" +
                "        while (i < 10) {\n" +
                "            i++;\n" +
                "            i+=1;\n" +
                "        }\n" +
                "\n" +
                "        while (i < 10) i++;\n" +
                "\n" +
                "        while (i < 10) i++;\n" +
                "\n" +
                "        for (int i = 0; i < 10; i++) {\n" +
                "            System.out.println(i);\n" +
                "            System.out.println(i);\n" +
                "        }\n" +
                "\n" +
                "        for (int i = 0; i < 10; i++) System.out.println(i);\n" +
                "\n" +
                "        for (int i = 0; i < 10; i++) System.out.println(i);\n" +
                "\n" +
                "        if (i < 10) {\n" +
                "            System.out.println(i);\n" +
                "            System.out.println(i);\n" +
                "        }\n" +
                "\n" +
                "        if (i < 10) System.out.println(i);\n" +
                "\n" +
                "        if (i < 10) System.out.println(i);\n" +
                "    }\n" +
                "}";
    }
}
