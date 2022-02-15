package usi.si.seart.utils;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.Node;
import lombok.experimental.UtilityClass;

import java.util.Spliterator;
import java.util.stream.StreamSupport;

@UtilityClass
public class NodeUtils {

    // TODO: 15.02.22 Should we count whitespaces and EOF as tokens?
    public Long countTokens(Node node) {
        return node.getTokenRange()
                .map(range -> {
                    Spliterator<JavaToken> spliterator = range.spliterator();
                    return StreamSupport.stream(spliterator, true).count();
                })
                .orElse(0L);
    }

    public Long countLines(Node node) {
        return node.getRange()
                .map(range -> (long)(range.end.line + 1 - range.begin.line))
                .orElse(0L);
    }
}
