package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.TreeCursor;
import ch.usi.si.seart.treesitter.printer.TreePrinter;
import lombok.Cleanup;

public class SyntaxTreePrinter extends AbstractPrinter {

    @Override
    public String print(Node node) {
        @Cleanup TreeCursor cursor = node.walk();
        TreePrinter printer = new ch.usi.si.seart.treesitter.printer.SyntaxTreePrinter(cursor);
        return printer.print();
    }
}
