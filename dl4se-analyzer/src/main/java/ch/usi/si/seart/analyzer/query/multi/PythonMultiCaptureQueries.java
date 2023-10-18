package ch.usi.si.seart.analyzer.query.multi;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Wildcard%20Node">
 *     Query Syntax - Wildcard Node
 * </a>
 * @see <a href="https://peps.python.org/pep-0318/">PEP 318 - Decorators for Functions and Methods</a>
 */
public class PythonMultiCaptureQueries extends MultiCaptureQueries {

    public PythonMultiCaptureQueries() {
        super(Language.PYTHON);
    }

    @Override
    public List<List<Pair<String, Node>>> getCallableDeclarations(Node node) {
        return execute(node, "(_ (decorator)* @additional (function_definition) @target)");
    }
}
