package ch.usi.si.seart.analyzer.query.single;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;

import java.util.List;

/**
 * @see
 * <a href="https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax:~:text=Alternations">
 *     Query Syntax - Alternations
 * </a>
 */
public class JavaSingleCaptureQueries extends SingleCaptureQueries {

    public JavaSingleCaptureQueries() {
        super(Language.JAVA);
    }

    @Override
    public List<Node> getComments(Node node) {
        return execute(node, "[(line_comment) (block_comment)] @comment");
    }
}
