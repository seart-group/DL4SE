package usi.si.seart.utils;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import lombok.experimental.UtilityClass;
import usi.si.seart.collection.Tuple;
import usi.si.seart.model.code.Boilerplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                    return StreamSupport.stream(spliterator, true).collect(Collectors.toList());
                })
                .orElse(new ArrayList<>());

        Map<Boolean, List<JavaToken>> partition = tokens.stream()
                .collect(Collectors.partitioningBy(token -> token.getCategory().isWhitespaceOrComment()));

        Long codeTokens = (long) partition.get(false).size();
        Long nonCodeTokens = partition.get(true).stream().mapToLong(token -> {
            JavaToken.Category category = token.getCategory();
            if (category.isWhitespace()) {
                return 1L;
            } else {
                String text = token.getText();
                String normalized = StringUtils.normalizeSpace(text);
                String[] words = normalized.split("\\s");
                long spaces = words.length - 1L;
                return words.length + spaces;
            }
        }).sum();

        return Tuple.of(codeTokens + nonCodeTokens, codeTokens);
    }

    public Long countLines(Node node) {
        return node.getRange()
                .map(range -> (long)(range.end.line + 1 - range.begin.line))
                .orElse(0L);
    }

    public Boilerplate getBoilerplateType(CallableDeclaration<?> node) {
        if (node instanceof ConstructorDeclaration) return Boilerplate.CONSTRUCTOR;
        String name = node.getNameAsString();
        if (name.startsWith("set")) return Boilerplate.SETTER;
        if (name.startsWith("get")) return Boilerplate.GETTER;
        switch (name) {
            case "builder": return Boilerplate.BUILDER;
            case "equals": return Boilerplate.EQUALS;
            case "hashCode": return Boilerplate.HASH_CODE;
            case "toString": return Boilerplate.TO_STRING;
            default: return null;
        }
    }
}
