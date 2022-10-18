package usi.si.seart.parser;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.XmlPrinter;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.collection.Tuple;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.model.code.Code;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.utils.PathUtils;
import usi.si.seart.utils.StringUtils;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class JavaParser extends AbstractParser {

    private static final XmlPrinter astPrinter = new XmlPrinter(true);

    public JavaParser(Language language) {
        super(language);
    }

    @Override
    public File parse(Path path) throws ParsingException {
        fileBuilder.isTest(PathUtils.isTestFile(path));

        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(path.toFile());
            fileBuilder.isParsed(true);
            new VoidVisitor().visit(compilationUnit, null);
        } catch (ParseProblemException | FileNotFoundException | StackOverflowError thr) {
            log.error("Parsing failed for: " + path, thr);
            throw new ParsingException(thr.getMessage(), thr.getCause());
        }

        return buildFileAndFunctions();
    }

    private class VoidVisitor extends VoidVisitorAdapter<Object> {

        @Override
        public void visit(CompilationUnit declaration, Object arg) {
            copyToBuilder(declaration, fileBuilder);

            super.visit(declaration, arg);
        }

        @Override
        public void visit(MethodDeclaration declaration, Object arg) {
            visit(declaration);
            super.visit(declaration, arg);
        }

        @Override
        public void visit(ConstructorDeclaration declaration, Object arg) {
            visit(declaration);
            super.visit(declaration, arg);
        }

        private void visit(CallableDeclaration<?> declaration) {
            Function.FunctionBuilder<?, ?> functionBuilder = Function.builder();

            copyToBuilder(declaration, functionBuilder);

            functionBuilder.boilerplateType(getBoilerplateType(declaration));

            functionBuilders.add(functionBuilder);
        }

        private void copyToBuilder(Node node, Code.CodeBuilder<?, ?> builder) {
            builder.language(language);

            String contents = node.toString();
            builder.content(contents);

            builder.ast(astPrinter.output(node));

            builder.characters(contents.chars().count());

            Tuple<Long, Long> tokensCount = countTokens(node);
            builder.totalTokens(tokensCount.getLeft());
            builder.codeTokens(tokensCount.getRight());

            builder.lines(countLines(node));

            builder.containsNonAscii(StringUtils.containsNonAscii(contents));

            removeComments(node);
            String normalized = StringUtils.normalizeSpace(node.toString());
            builder.contentHash(StringUtils.sha256(normalized));
            builder.astHash(getAstHash(node));
        }
    }

    static String getAstHash(Node node) {
        StringBuilder builder = new StringBuilder();
        getAstTypeNames(node, builder);
        return StringUtils.sha256(builder.toString());
    }

    private static void getAstTypeNames(Node node, StringBuilder builder) {
        builder.append(node.getMetaModel().getTypeName());
        List<Node> children = node.getChildNodes();
        for (Node child : children) {
            getAstTypeNames(child, builder);
        }
    }

    static Tuple<Long, Long> countTokens(Node node) {
        if (node instanceof CompilationUnit) {
            return rangeLength(node);
        } else if (node instanceof CallableDeclaration) {
            return countTokens((CallableDeclaration<?>) node);
        } else {
            throw new UnsupportedOperationException("Token counting is not supported at this granularity level!");
        }
    }

    private static Tuple<Long, Long> countTokens(CallableDeclaration<?> cd) {
        Tuple<Long, Long> count = rangeLength(cd);
        Optional<Comment> comment = cd.getComment();

        if (comment.isPresent()) {
            String jdoc = comment.get().toString();
            Long jdocLen = countWordsAndSpaces(jdoc);
            count = Tuple.of(count.getLeft() + jdocLen, count.getRight());
        }

        return count;
    }

    private static Tuple<Long, Long> rangeLength(Node node) {
        List<JavaToken> tokens = getNodeTokens(node);
        Map<Boolean, List<JavaToken>> partition = tokens.stream()
                .collect(Collectors.partitioningBy(token -> token.getCategory().isWhitespaceOrComment()));

        long codeTokens = partition.get(false).size();
        long nonCodeTokens = partition.get(true).stream().mapToLong(token -> {
            JavaToken.Category category = token.getCategory();
            if (category.isWhitespace()) {
                return 1L;
            } else {
                return countWordsAndSpaces(token.getText());
            }
        }).sum();

        return Tuple.of(codeTokens + nonCodeTokens, codeTokens);
    }

    private static List<JavaToken> getNodeTokens(Node node) {
        return node.getTokenRange()
                .map(range -> {
                    Spliterator<JavaToken> spliterator = range.spliterator();
                    return StreamSupport.stream(spliterator, false).collect(Collectors.toList());
                })
                .orElse(new ArrayList<>());
    }

    private static long countWordsAndSpaces(String input) {
        if (input.isBlank()) return 0L;
        String normalized = StringUtils.normalizeSpace(input);
        String[] words = normalized.split("\\s");
        long spaces = words.length - 1L;
        return words.length + spaces;
    }

    static Long countLines(Node node) {
        return node.getRange()
                .map(range -> (long)(range.end.line + 1 - range.begin.line))
                .orElse(0L);
    }

    static Boilerplate getBoilerplateType(CallableDeclaration<?> node) {
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

    static void removeComments(Node node) {
        List<Comment> comments = node.getAllContainedComments();
        node.getComment().ifPresent(comments::add);
        comments.forEach(Comment::remove);
    }
}
