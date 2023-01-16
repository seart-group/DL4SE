package usi.si.seart.analyzer.query;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Query;
import usi.si.seart.treesitter.QueryCapture;
import usi.si.seart.treesitter.QueryCursor;
import usi.si.seart.treesitter.QueryMatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SingleCaptureQueries implements Queries<List<Node>>, Validated {

    private final Language language;

    @Override
    public List<Node> getNodes(Node node) {
        return execute(node, "(_) @node");
    }

    protected List<Node> execute(Node node, String sExpr) {
        validate(sExpr, "Queries must contain at least one capture!");
        @Cleanup Query query = new Query(this.language, sExpr);
        @Cleanup QueryCursor cursor = new QueryCursor();
        cursor.execQuery(query, node);
        List<QueryMatch> matches = new ArrayList<>();
        QueryMatch match;
        while ((match = cursor.nextMatch()) != null)
            matches.add(match);
        return matches.stream()
                .map(QueryMatch::getCaptures)
                .flatMap(Arrays::stream)
                .map(QueryCapture::getNode)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isPastThreshold(int count) {
        return count >= 1;
    }

    public static SingleCaptureQueries forLanguage(Language language) {
        switch (language) {
            case JAVA: return new JavaSingleCaptureQueries();
            default: return new SingleCaptureQueries(null) {

                @Override
                public List<Node> getNodes(Node node) {
                    return List.of();
                }

                @Override
                public List<Node> getComments(Node node) {
                    return List.of();
                }

                @Override
                public List<Node> getCallableDeclarations(Node node) {
                    return List.of();
                }
            };
        }
    }
}
