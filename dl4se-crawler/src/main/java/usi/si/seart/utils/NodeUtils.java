package usi.si.seart.utils;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

@UtilityClass
public class NodeUtils {

    public String getAstHash(Node node) {
        List<String> types = getAstTypeNames(node, new ArrayList<>());
        return StringUtils.sha256(String.join("", types));
    }

    private List<String> getAstTypeNames(Node node, List<String> types) {
        types.add(node.getMetaModel().getTypeName());
        List<Node> children = node.getChildNodes();
        for (Node child : children) {
            getAstTypeNames(child, types);
        }
        return types;
    }

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
