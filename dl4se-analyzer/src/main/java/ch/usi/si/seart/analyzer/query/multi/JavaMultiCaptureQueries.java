package ch.usi.si.seart.analyzer.query.multi;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import org.apache.commons.lang3.tuple.Pair;

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
 */
public class JavaMultiCaptureQueries extends MultiCaptureQueries {

    public JavaMultiCaptureQueries() {
        super(Language.JAVA);
    }

    @Override
    public List<List<Pair<String, Node>>> getCallableDeclarations(Node node) {
        return execute(
                node,
                "([(line_comment)(block_comment)]* @additional . (method_declaration) @target)" +
                "([(line_comment)(block_comment)]* @additional . (constructor_declaration) @target)"
        );
    }
}
