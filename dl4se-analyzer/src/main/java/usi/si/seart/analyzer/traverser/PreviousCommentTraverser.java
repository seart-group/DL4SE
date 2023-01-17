package usi.si.seart.analyzer.traverser;

import usi.si.seart.treesitter.Node;

import java.util.ArrayList;
import java.util.List;

public class PreviousCommentTraverser implements Traverser<List<Node>> {

    @Override
    public List<Node> getNodes(Node node) {
        List<Node> results = new ArrayList<>();
        Node current = node;
        while (current != null) {
            Node previous = current.getPrevSibling();
            if (previous == null) break;
            String type = previous.getType();
            if (!type.contains("comment")) break;
            results.add(previous);
            current = previous;
        }
        return results;
    }
}
