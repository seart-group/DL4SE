package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Tree;

import java.util.List;
import java.util.stream.Collectors;

public abstract class JavaExtractor extends QueryBasedExtractor {

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

    protected JavaExtractor() {
        super(Language.JAVA);
    }

    @Override
    protected Node getStartingNode(Tree tree) {
        Node root = tree.getRootNode();
        List<Node> declarations = root.getNamedChildren().stream()
                .filter(JavaExtractor::isDeclaration)
                .collect(Collectors.toUnmodifiableList());
        return declarations.size() == 1 ? declarations.get(0) : root;
    }
}
