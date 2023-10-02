package usi.si.seart.analyzer.query.single;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Query;
import ch.usi.si.seart.treesitter.QueryCursor;
import ch.usi.si.seart.treesitter.QueryMatch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import usi.si.seart.analyzer.query.Queries;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SingleCaptureQueries implements Queries<List<Node>> {

    private final Language language;

    public abstract List<Node> getComments(Node node);

    public List<Node> execute(Node node, String pattern) {
        @Cleanup Query query = Query.getFor(language, pattern);
        verify(query);
        @Cleanup QueryCursor cursor = node.walk(query);
        Stream<QueryMatch> matches = StreamSupport.stream(cursor.spliterator(), false);
        return matches.map(QueryMatch::getNodes)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static SingleCaptureQueries getInstance(Language language) {
        switch (language) {
            case JAVA:
                return new JavaSingleCaptureQueries();
            case PYTHON:
                return new PythonSingleCaptureQueries();
            default:
                return new SingleCaptureQueries(null) {

                    @Override
                    public List<Node> getComments(Node node) {
                        return Collections.emptyList();
                    }
                };
        }
    }
}
