package ch.usi.si.seart.analyzer.extract;

import java.util.Collections;
import java.util.List;

/**
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Wildcard%20Node">
 *     Query Syntax - Wildcard Node
 * </a>
 * @see <a href="https://peps.python.org/pep-0318/">PEP 318 - Decorators for Functions and Methods</a>
 */
public class PythonFunctionExtractor extends PythonExtractor {

    @Override
    protected List<String> getPatterns() {
        return Collections.singletonList("(_ (decorator)* @additional (function_definition) @target)");
    }
}
