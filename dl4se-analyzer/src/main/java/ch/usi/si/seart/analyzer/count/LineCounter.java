package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Point;
import ch.usi.si.seart.treesitter.Range;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineCounter extends AbstractCounter {

    @Override
    public Long count(Node node) {
        Range range = node.getRange();
        Span span = new Span(range);
        return count(span);
    }

    @Override
    protected Long count(Stream<Node> nodes) {
        List<Span> spans = nodes.map(Node::getRange)
                .map(Span::new)
                .collect(Collectors.toList());
        return merge(spans).stream()
                .mapToLong(this::count)
                .sum();
    }

    private Long count(Span span) {
        Point startPoint = span.getStart();
        Point endPoint = span.getEnd();
        long startRow = startPoint.getRow();
        long endRow = endPoint.getRow();
        return endRow - startRow + 1;
    }

    private static List<Span> merge(List<Span> spans) {
        List<Span> result = new ArrayList<>();
        spans.stream().reduce((left, right) -> {
            int leftRow = left.getEnd().getRow();
            int rightRow = right.getStart().getRow();
            if (leftRow == rightRow)
                return new Span(left.getStart(), right.getEnd());
            result.add(left);
            return right;
        }).ifPresent(result::add);
        return result;
    }

    @Getter(AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class Span {

        Point start;
        Point end;

        public Span(Range range) {
            this(range.getStartPoint(), range.getEndPoint());
        }
    }
}
