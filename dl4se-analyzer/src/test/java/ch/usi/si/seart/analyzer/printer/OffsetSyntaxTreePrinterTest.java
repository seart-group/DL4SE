package ch.usi.si.seart.analyzer.printer;

import ch.usi.si.seart.analyzer.JavaBaseTest;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Point;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OffsetSyntaxTreePrinterTest extends JavaBaseTest {

    @Test
    void idempotencyTest() {
        Printer printer = new OffsetSyntaxTreePrinter(new Point(0, 0));
        Node root = tree.getRootNode();
        Node method = root.getChild(1).getChildByFieldName("body").getChild(1);
        String actual = printer.print(method);
        String expected =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                OffsetSyntaxTreePrinter.TAG_OPEN +
                    "<method_declaration start=\"3:4\" end=\"6:5\">" +
                        "<modifiers start=\"3:4\" end=\"3:17\">" +
                        "</modifiers>" +
                        "<void_type name=\"type\" start=\"3:18\" end=\"3:22\">" +
                        "</void_type>" +
                        "<identifier name=\"name\" start=\"3:23\" end=\"3:27\">" +
                        "</identifier>" +
                        "<formal_parameters name=\"parameters\" start=\"3:27\" end=\"3:42\">" +
                            "<formal_parameter start=\"3:28\" end=\"3:41\">" +
                                "<array_type name=\"type\" start=\"3:28\" end=\"3:36\">" +
                                    "<type_identifier name=\"element\" start=\"3:28\" end=\"3:34\">" +
                                    "</type_identifier>" +
                                    "<dimensions name=\"dimensions\" start=\"3:34\" end=\"3:36\">" +
                                    "</dimensions>" +
                                "</array_type>" +
                                "<identifier name=\"name\" start=\"3:37\" end=\"3:41\">" +
                                "</identifier>" +
                            "</formal_parameter>" +
                        "</formal_parameters>" +
                        "<block name=\"body\" start=\"3:43\" end=\"6:5\">" +
                            "<line_comment start=\"4:8\" end=\"4:22\">" +
                            "</line_comment>" +
                            "<expression_statement start=\"5:8\" end=\"5:44\">" +
                                "<method_invocation start=\"5:8\" end=\"5:43\">" +
                                    "<field_access name=\"object\" start=\"5:8\" end=\"5:18\">" +
                                        "<identifier name=\"object\" start=\"5:8\" end=\"5:14\">" +
                                        "</identifier>" +
                                        "<identifier name=\"field\" start=\"5:15\" end=\"5:18\">" +
                                        "</identifier>" +
                                    "</field_access>" +
                                    "<identifier name=\"name\" start=\"5:19\" end=\"5:26\">" +
                                    "</identifier>" +
                                    "<argument_list name=\"arguments\" start=\"5:26\" end=\"5:43\">" +
                                        "<string_literal start=\"5:27\" end=\"5:42\">" +
                                        "</string_literal>" +
                                    "</argument_list>" +
                                "</method_invocation>" +
                            "</expression_statement>" +
                        "</block>" +
                    "</method_declaration>" +
                OffsetSyntaxTreePrinter.TAG_CLOSE;
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void offsetTest() {
        Printer printer = new OffsetSyntaxTreePrinter(new Point(-1, -2));
        Node root = tree.getRootNode();
        Node method = root.getChild(1).getChildByFieldName("body").getChild(1);
        String actual = printer.print(method);
        String expected =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                OffsetSyntaxTreePrinter.TAG_OPEN +
                    "<method_declaration start=\"2:2\" end=\"5:3\">" +
                        "<modifiers start=\"2:2\" end=\"2:15\">" +
                        "</modifiers>" +
                        "<void_type name=\"type\" start=\"2:16\" end=\"2:20\">" +
                        "</void_type>" +
                        "<identifier name=\"name\" start=\"2:21\" end=\"2:25\">" +
                        "</identifier>" +
                        "<formal_parameters name=\"parameters\" start=\"2:25\" end=\"2:40\">" +
                            "<formal_parameter start=\"2:26\" end=\"2:39\">" +
                                "<array_type name=\"type\" start=\"2:26\" end=\"2:34\">" +
                                    "<type_identifier name=\"element\" start=\"2:26\" end=\"2:32\">" +
                                    "</type_identifier>" +
                                    "<dimensions name=\"dimensions\" start=\"2:32\" end=\"2:34\">" +
                                    "</dimensions>" +
                                "</array_type>" +
                                "<identifier name=\"name\" start=\"2:35\" end=\"2:39\">" +
                                "</identifier>" +
                            "</formal_parameter>" +
                        "</formal_parameters>" +
                        "<block name=\"body\" start=\"2:41\" end=\"5:3\">" +
                            "<line_comment start=\"3:6\" end=\"3:20\">" +
                            "</line_comment>" +
                            "<expression_statement start=\"4:6\" end=\"4:42\">" +
                                "<method_invocation start=\"4:6\" end=\"4:41\">" +
                                    "<field_access name=\"object\" start=\"4:6\" end=\"4:16\">" +
                                        "<identifier name=\"object\" start=\"4:6\" end=\"4:12\">" +
                                        "</identifier>" +
                                        "<identifier name=\"field\" start=\"4:13\" end=\"4:16\">" +
                                        "</identifier>" +
                                    "</field_access>" +
                                    "<identifier name=\"name\" start=\"4:17\" end=\"4:24\">" +
                                    "</identifier>" +
                                    "<argument_list name=\"arguments\" start=\"4:24\" end=\"4:41\">" +
                                        "<string_literal start=\"4:25\" end=\"4:40\">" +
                                        "</string_literal>" +
                                    "</argument_list>" +
                                "</method_invocation>" +
                            "</expression_statement>" +
                        "</block>" +
                    "</method_declaration>" +
                OffsetSyntaxTreePrinter.TAG_CLOSE;
        Assertions.assertEquals(expected, actual);
    }
}
