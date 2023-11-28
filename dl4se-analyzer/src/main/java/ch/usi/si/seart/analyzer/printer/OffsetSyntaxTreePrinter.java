package ch.usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.OffsetTreeCursor;
import ch.usi.si.seart.treesitter.Point;
import ch.usi.si.seart.treesitter.TreeCursor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OffsetSyntaxTreePrinter extends SyntaxTreePrinter {

    Point offset;

    @Override
    protected TreeCursor getCursor(Node node) {
        return new OffsetTreeCursor(node, offset);
    }
}
