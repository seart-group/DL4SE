package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Tree;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class JavaCallableDeclarationExtractor extends JavaExtractor {

    private static String substitute(String type) {
        return String.format("([(line_comment)(block_comment)]* @additional . (%s) @target)", type);
    }

    private static boolean isDeclaration(Node node) {
        switch (node.getType()) {
            case "annotation_type_declaration":
            case "class_declaration":
            case "enum_declaration":
            case "interface_declaration":
                return true;
            default:
                return false;
        }
    }

    @Override
    protected List<String> getPatterns() {
        return Stream.of("method_declaration", "constructor_declaration")
                .map(JavaCallableDeclarationExtractor::substitute)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    protected Node getStartingNode(Tree tree) {
        Node root = tree.getRootNode();
        List<Node> declarations = root.getNamedChildren().stream()
                .filter(JavaCallableDeclarationExtractor::isDeclaration)
                .collect(Collectors.toUnmodifiableList());
        return declarations.size() == 1 ? declarations.get(0) : root;
    }
}
