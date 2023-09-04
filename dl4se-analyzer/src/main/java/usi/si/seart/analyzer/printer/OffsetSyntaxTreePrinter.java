package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.OffsetTreeCursor;
import ch.usi.si.seart.treesitter.Point;
import ch.usi.si.seart.treesitter.printer.TreePrinter;
import lombok.AllArgsConstructor;
import lombok.Cleanup;

@AllArgsConstructor
public class OffsetSyntaxTreePrinter extends SyntaxTreePrinter {

    private final Point offset;

    @Override
    public String print(Node node) {
        @Cleanup OffsetTreeCursor cursor = new OffsetTreeCursor(node, offset);
        TreePrinter printer = new ch.usi.si.seart.treesitter.printer.SyntaxTreePrinter(cursor);
        return printer.print();
    }
}
