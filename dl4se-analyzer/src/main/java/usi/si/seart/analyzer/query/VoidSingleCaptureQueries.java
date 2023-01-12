package usi.si.seart.analyzer.query;

import usi.si.seart.treesitter.Node;

import java.util.List;

public class VoidSingleCaptureQueries extends SingleCaptureQueries {

    private static final List<Node> NO_RESULTS = List.of();

    public VoidSingleCaptureQueries() {
        super(null);
    }

    @Override
    public List<Node> getNodes(Node node) {
        return NO_RESULTS;
    }

    @Override
    public List<Node> getComments(Node node) {
        return NO_RESULTS;
    }

    @Override
    public List<Node> getCallableDeclarations(Node node) {
        return NO_RESULTS;
    }
}
