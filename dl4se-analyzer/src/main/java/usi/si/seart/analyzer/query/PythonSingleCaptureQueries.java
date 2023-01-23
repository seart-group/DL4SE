package usi.si.seart.analyzer.query;

import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;

import java.util.List;

/**
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Anchors">
 *     Query Syntax - Anchors
 * </a>
 * @see <a href="https://peps.python.org/pep-0257/">PEP 257 – Docstring Conventions</a>
 */
public class PythonSingleCaptureQueries extends SingleCaptureQueries {

    protected PythonSingleCaptureQueries() {
        super(Language.PYTHON);
    }

    @Override
    public List<Node> getComments(Node node) {
        return execute(
                node,
                "[" +
                        "(_ (comment) @comment)" +
                        "(module . (expression_statement (string) @comment))" +
                        "(class_definition body: (block . (expression_statement (string) @comment)))" +
                        "(function_definition body: (block . (expression_statement (string) @comment)))" +
                "]"
        );
    }

    @Override
    public List<Node> getCallableDeclarations(Node node) {
        return execute(node, "(function_definition) @definition");
    }
}
