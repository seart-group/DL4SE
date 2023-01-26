package usi.si.seart.analyzer.printer;

import usi.si.seart.treesitter.Node;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SExpressionPrinter extends AbstractPrinter {

    @Override
    public String print(Node node) {
        return node.getNodeString();
    }

    @Override
    public Collector<CharSequence, ?, String> resultCollector() {
        return Collectors.joining(" ");
    }
}
