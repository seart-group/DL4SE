package usi.si.seart.analyzer.printer;

import usi.si.seart.treesitter.Node;

public class SExpressionPrinter implements Printer {

    @Override
    public String print(Node node) {
        return node.getNodeString();
    }
}
