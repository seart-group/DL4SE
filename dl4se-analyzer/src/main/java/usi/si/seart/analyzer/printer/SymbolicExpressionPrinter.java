package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import usi.si.seart.analyzer.util.stream.MinimumSizeLimitCollector;

import java.util.stream.Collector;

public class SymbolicExpressionPrinter extends AbstractPrinter {

    @Override
    public String print(Node node) {
        return node.getNodeString();
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
