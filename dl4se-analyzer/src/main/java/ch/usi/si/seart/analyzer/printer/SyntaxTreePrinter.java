package ch.usi.si.seart.analyzer.printer;

import ch.usi.si.seart.analyzer.util.stream.MinimumSizeLimitCollector;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.TreeCursor;
import ch.usi.si.seart.treesitter.printer.TreePrinter;
import ch.usi.si.seart.treesitter.printer.XMLPrinter;
import lombok.Cleanup;

import java.util.stream.Collector;
import java.util.stream.Stream;

public class SyntaxTreePrinter extends AbstractPrinter {

    private static final int BEGIN_INDEX = XMLPrinter.PROLOG.length();

    public static final String TAG_OPEN = "<ast>";
    public static final String TAG_CLOSE = "</ast>";

    @Override
    public String print(Node node) {
        return print(Stream.of(node));
    }

    protected TreeCursor getCursor(Node node) {
        return node.walk();
    }

    @Override
    protected String print(Stream<Node> nodes) {
        return nodes.map(this::printWithoutProlog).collect(resultCollector());
    }

    private String printWithoutProlog(Node node) {
        @Cleanup TreeCursor cursor = getCursor(node);
        TreePrinter printer = new XMLPrinter(cursor);
        return printer.print().substring(BEGIN_INDEX);
    }

    @Override
    protected Collector<CharSequence, ?, String> resultCollector() {
        return new MinimumSizeLimitCollector(1) {

            @Override
            protected String getDelimiter() {
                return "";
            }

            @Override
            protected String getPrefix() {
                return XMLPrinter.PROLOG + TAG_OPEN;
            }

            @Override
            protected String getSuffix() {
                return TAG_CLOSE;
            }
        };
    }
}
