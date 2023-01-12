package usi.si.seart.analyzer.query;

import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;

import java.util.List;

public class JavaSingleCaptureQueries extends SingleCaptureQueries {

    public JavaSingleCaptureQueries() {
        super(Language.JAVA);
    }

    @Override
    public List<Node> getComments(Node node) {
        return execute(node, "[(line_comment) (block_comment)] @comment");
    }

    @Override
    public List<Node> getCallableDeclarations(Node node) {
        return execute(node, "[(constructor_declaration) (method_declaration)] @declaration");
    }
}
