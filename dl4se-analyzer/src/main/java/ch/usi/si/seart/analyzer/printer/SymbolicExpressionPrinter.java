package ch.usi.si.seart.analyzer.printer;

import ch.usi.si.seart.analyzer.util.stream.MinimumSizeLimitCollector;
import ch.usi.si.seart.treesitter.Node;

import java.util.stream.Collector;

public class SymbolicExpressionPrinter extends AbstractPrinter {

    @Override
    public String print(Node node) {
        return new ch.usi.si.seart.treesitter.printer.SymbolicExpressionPrinter(node.walk()).print();
    }

    @Override
    protected Collector<CharSequence, ?, String> resultCollector() {
        return new MinimumSizeLimitCollector(2) {

            @Override
            protected String getDelimiter() {
                return " ";
            }

            @Override
            protected String getPrefix() {
                return "(";
            }

            @Override
            protected String getSuffix() {
                return ")";
            }
        };
    }
}
