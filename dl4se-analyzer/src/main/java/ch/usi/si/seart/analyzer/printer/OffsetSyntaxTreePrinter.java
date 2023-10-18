package ch.usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.OffsetTreeCursor;
import ch.usi.si.seart.treesitter.Point;
import ch.usi.si.seart.treesitter.TreeCursor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OffsetSyntaxTreePrinter extends SyntaxTreePrinter {

    private final Point offset;

    @Override
    protected TreeCursor getCursor(Node node) {
        return new OffsetTreeCursor(node, offset);
    }
}
