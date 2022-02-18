package usi.si.seart.utils;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import lombok.experimental.UtilityClass;
import usi.si.seart.collection.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@UtilityClass
public class NodeUtils {

    public String getAstHash(Node node) {
        StringBuilder builder = new StringBuilder();
        getAstTypeNames(node, builder);
        return StringUtils.sha256(builder.toString());
    }

    private void getAstTypeNames(Node node, StringBuilder builder) {
        builder.append(node.getMetaModel().getTypeName());
        List<Node> children = node.getChildNodes();
        for (Node child : children) {
            getAstTypeNames(child, builder);
        }
    }

    public Tuple<Long, Long> countTokens(Node node) {
        List<JavaToken> tokens = node.getTokenRange()
                .map(range -> {
                    Spliterator<JavaToken> spliterator = range.spliterator();
                    return StreamSupport.stream(spliterator, true);
                })
                .map(stream -> stream.collect(Collectors.toList()))
                .orElse(new ArrayList<>());
        Long allTokens = (long) tokens.size();
        Long nonCodeTokens = tokens.stream()
                .map(JavaToken::getCategory)
                .filter(JavaToken.Category::isWhitespaceOrComment)
                .count();
        return Tuple.of(allTokens, allTokens - nonCodeTokens);
    }

    public Long countLines(Node node) {
        return node.getRange()
                .map(range -> (long)(range.end.line + 1 - range.begin.line))
                .orElse(0L);
    }

    // TODO: 15.02.22 Other determiners for boilerplate functions?
    public boolean isBoilerplate(CallableDeclaration<?> node) {
        String name = node.getNameAsString();
        return node instanceof ConstructorDeclaration ||
                name.startsWith("set") ||
                name.startsWith("get") ||
                name.equals("equals") ||
                name.equals("hashCode") ||
                name.equals("toString") ||
                name.equals("builder") ||
                name.equals("build");
    }
}
