package usi.si.seart.converter;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.printer.XmlPrinter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.antlr.v4.runtime.Token;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.function.CodeProcessingPipeline;
import usi.si.seart.model.task.CodeTask;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.processing.CodeProcessing;
import usi.si.seart.model.task.processing.Processing;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.task.query.FileQuery;
import usi.si.seart.model.task.query.FunctionQuery;
import usi.si.seart.model.task.query.Query;
import usi.si.seart.src2abs.Abstractor;
import usi.si.seart.src2abs.Parser;
import usi.si.seart.src2abs.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TaskToProcessingPipelineConverter implements Converter<Task, CodeProcessingPipeline> {

    private static final XmlPrinter astPrinter = new XmlPrinter(true);

    @Override
    @NonNull
    public CodeProcessingPipeline convert(@NonNull Task source) {
        if (source instanceof CodeTask) {
            return convert((CodeTask) source);
        } else {
            throw new UnsupportedOperationException(
                    "Converter not implemented for task type: " + source.getClass().getName()
            );
        }
    }

    private CodeProcessingPipeline convert(CodeTask source) {
        Query query = source.getQuery();
        Processing processing = source.getProcessing();
        if (query instanceof CodeQuery && processing instanceof CodeProcessing) {
            return convert((CodeQuery) query, (CodeProcessing) processing);
        } else {
            throw new IllegalArgumentException(String.format(
                    "No processing mapping for (query, processing) tuple types: (%s, %s)",
                    query.getClass().getName(),
                    processing.getClass().getName()
            ));
        }
    }

    private CodeProcessingPipeline convert(CodeQuery codeQuery, CodeProcessing codeProcessing) {
        boolean includeAst = codeQuery.getIncludeAst();
        if (codeQuery instanceof FileQuery) {
            return convert(codeProcessing, includeAst, StaticJavaParser::parse);
        } else if (codeQuery instanceof FunctionQuery) {
            return convert(codeProcessing, includeAst, StaticJavaParser::parseMethodDeclaration);
        } else {
            throw new UnsupportedOperationException(
                    "Converter not implemented for code granularity: " + codeProcessing.getClass().getName()
            );
        }
    }

    // TODO 06.10.22: Extract each of the added pipeline functions into its own separate class
    private CodeProcessingPipeline convert(
            CodeProcessing codeProcessing, boolean includeAst, java.util.function.Function<String, Node> parser
    ) {
        CodeProcessingPipeline pipeline = new CodeProcessingPipeline();

        if (!includeAst) {
            pipeline.add(map -> {
                map.remove("ast");
                return map;
            });
        }

        boolean removeDocstring = codeProcessing.getRemoveDocstring();
        boolean removeInnerComments = codeProcessing.getRemoveInnerComments();
        boolean abstractCode = codeProcessing.getAbstractCode();
        boolean maskCode = codeProcessing.getMaskPercentage() != null;

        if (removeDocstring || removeInnerComments) {
            pipeline.add(map -> {
                try {
                    String content = (String) map.get("content");
                    Node node = parser.apply(content);
                    Predicate<Comment> predicate = generateCommentPredicate(removeDocstring, removeInnerComments);
                    List<Comment> comments = node.getAllContainedComments();
                    node.getComment().ifPresent(comments::add);
                    comments.stream().filter(predicate).forEach(Comment::remove);
                    map.put("content", node.toString());
                    if (includeAst) {
                        map.put("ast", astPrinter.output(node));
                    }
                } catch (ParseProblemException | StackOverflowError ignore) {}
                return map;
            });
        }

        if (abstractCode) {
            pipeline.add(map -> {
                try {
                    String sourceCode = (String) map.get("content");
                    String cleanedCode = Abstractor.cleanCode(sourceCode);
                    Set<String> idioms = new TreeSet<>(codeProcessing.getAbstractIdioms());
                    Parser absParser = new Parser();
                    absParser.parse(cleanedCode, parser);
                    Tokenizer tokenizer = new Tokenizer(absParser, idioms);
                    Node node = parser.apply(tokenizer.tokenize(sourceCode));
                    map.put("content", node.toString());
                    if (includeAst) {
                        map.put("ast", astPrinter.output(node));
                    }
                    map.put("abstractions", tokenizer.export());
                } catch (ParseProblemException | StackOverflowError ignore) {}
                return map;
            });
        }

        // TODO 29.07.22: Use alternative method that does not remove comments
        if (maskCode) {
            pipeline.add(map -> {
                try {
                    String sourceCode = (String) map.get("content");
                    // TODO 12.10.22: Use a utility for this instead
                    sourceCode = sourceCode.replaceAll("\\s+", " ");
                    int percentage = codeProcessing.getMaskPercentage();
                    boolean contiguous = codeProcessing.getMaskContiguousOnly();
                    String maskToken = codeProcessing.getMaskToken();
                    List<Token> tokens = Tokenizer.readTokens(sourceCode);
                    List<TokenWrapper> wrapped = generateSelector(percentage, contiguous).apply(tokens);
                    LinkedList<LinkedList<TokenWrapper>> groupedWrapped = wrapped.stream().collect(
                            LinkedList::new,
                            (lists, wrapper) -> {
                                if (lists.isEmpty()) {
                                    withNewList(lists, wrapper);
                                } else {
                                    withNewString(lists, wrapper);
                                }
                            },
                            LinkedList::addAll
                    );

                    map.put("content", mask(groupedWrapped, maskToken));
                    map.put("oracle", oracle(groupedWrapped, maskToken));
                    map.remove("ast");
                } catch (StackOverflowError ignore) {}
                return map;
            });
        }

        return pipeline;
    }

    private static final Predicate<Comment> IS_ANY_COMMENT = comment -> true;
    private static final Predicate<Comment> IS_LINE_COMMENT = Comment::isLineComment;
    private static final Predicate<Comment> IS_BLOCK_COMMENT = Comment::isBlockComment;
    private static final Predicate<Comment> IS_JAVADOC_COMMENT = Comment::isJavadocComment;
    private static Predicate<Comment> generateCommentPredicate(boolean removeDocstring, boolean removeInnerComments) {
        Predicate<Comment> predicate;
        if (removeInnerComments && removeDocstring) {
            predicate = IS_ANY_COMMENT;
        } else if (removeDocstring) {
            predicate = IS_JAVADOC_COMMENT;
        } else {
            predicate = IS_LINE_COMMENT.or(IS_BLOCK_COMMENT);
        }
        return predicate;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class TokenWrapper {

        Token token;
        int ordinal;
        @NonFinal
        boolean selected = false;

        public void select() {
            selected = true;
        }
    }

    private Function<List<Token>, List<TokenWrapper>> generateSelector(int percentage, boolean contiguous) {
        return tokens -> {
            List<TokenWrapper> wrapped = new ArrayList<>(tokens.size());
            for (int i = 0; i < tokens.size(); i++) {
                wrapped.add(new TokenWrapper(tokens.get(i), i));
            }
            if (!contiguous) Collections.shuffle(wrapped);
            int startMax = wrapped.size() * (100 - percentage) / 100;
            for (
                    int i = ThreadLocalRandom.current().nextInt(startMax + 1), selected = 0;
                    (i < wrapped.size()) && (selected < (wrapped.size() * percentage / 100));
                    i++, selected++
            ) {
                wrapped.get(i).select();
            }
            if (!contiguous) wrapped.sort(Comparator.comparingInt(TokenWrapper::getOrdinal));
            return wrapped;
        };
    }

    private void withNewList(LinkedList<LinkedList<TokenWrapper>> groups, TokenWrapper token) {
        LinkedList<TokenWrapper> group = new LinkedList<>();
        group.add(token);
        groups.add(group);
    }

    private void withNewString(LinkedList<LinkedList<TokenWrapper>> groups, TokenWrapper token) {
        LinkedList<TokenWrapper> prevGroup = groups.getLast();
        if (prevGroup.getLast().isSelected() == token.isSelected()) {
            prevGroup.add(token);
        } else {
            withNewList(groups, token);
        }
    }

    private String mask(LinkedList<LinkedList<TokenWrapper>> groups, String maskToken) {
        return applyMasking(groups, (group) -> group.getFirst().isSelected(), maskToken);
    }

    private String oracle(LinkedList<LinkedList<TokenWrapper>> groups, String maskToken) {
        return applyMasking(groups, (group) -> !group.getFirst().isSelected(), maskToken);
    }

    private String applyMasking(
            LinkedList<LinkedList<TokenWrapper>> groups, Predicate<LinkedList<TokenWrapper>> predicate, String maskToken
    ) {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 1;
        for (LinkedList<TokenWrapper> group : groups) {
            if (predicate.test(group)) {
                stringBuilder.append("<").append(maskToken).append("_").append(count).append("> ");
                count += 1;
            } else {
                List<Token> tokens = group.stream()
                        .map(TokenWrapper::getToken)
                        .collect(Collectors.toList());
                String joined = joinTokens(tokens);
                stringBuilder.append(joined).append(" ");
            }
        }
        return stringBuilder.toString().stripTrailing();
    }

    private String joinTokens(List<Token> tokens) {
        StringBuilder stringBuilder = new StringBuilder();
        Token prev = tokens.get(0);
        stringBuilder.append(prev.getText());
        for (int i = 1; i < tokens.size(); i++) {
            Token curr = tokens.get(i);
            if (curr.getStartIndex() - prev.getStopIndex() > 1)
                stringBuilder.append(" ");
            stringBuilder.append(curr.getText());
            prev = curr;
        }
        return stringBuilder.toString();
    }
}
