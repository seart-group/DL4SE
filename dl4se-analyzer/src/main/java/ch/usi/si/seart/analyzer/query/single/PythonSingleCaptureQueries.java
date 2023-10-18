package ch.usi.si.seart.analyzer.query.single;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;

import java.util.List;

/**
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Alternations">
 *     Query Syntax - Alternations
 * </a>
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Anchors">
 *     Query Syntax - Anchors
 * </a>
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Wildcard%20Node">
 *     Query Syntax - Wildcard Node
 * </a>
 * @see <a href="https://peps.python.org/pep-0257/">PEP 257 – Docstring Conventions</a>
 */
public class PythonSingleCaptureQueries extends SingleCaptureQueries {

    public PythonSingleCaptureQueries() {
        super(Language.PYTHON);
    }

    @Override
    public List<Node> getComments(Node node) {
        return execute(
                node,
                "[" +
                        "(_ (comment) @comment)" +
                        "(module (comment)? . (expression_statement (string) @comment))" +
                        "(class_definition body: (block . (expression_statement (string) @comment)))" +
                        "(function_definition body: (block . (expression_statement (string) @comment)))" +
                "]"
        );
    }
}
