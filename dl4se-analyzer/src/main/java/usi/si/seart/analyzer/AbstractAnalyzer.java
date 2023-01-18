package usi.si.seart.analyzer;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import usi.si.seart.analyzer.count.CharacterCounter;
import usi.si.seart.analyzer.count.CodeTokenCounter;
import usi.si.seart.analyzer.count.Counter;
import usi.si.seart.analyzer.count.LineCounter;
import usi.si.seart.analyzer.count.TokenCounter;
import usi.si.seart.analyzer.enumerator.Enumerator;
import usi.si.seart.analyzer.hash.ContentHasher;
import usi.si.seart.analyzer.hash.Hasher;
import usi.si.seart.analyzer.hash.SyntaxTreeHasher;
import usi.si.seart.analyzer.predicate.ContainsErrorPredicate;
import usi.si.seart.analyzer.predicate.ContainsNonAsciiPredicate;
import usi.si.seart.analyzer.predicate.TestFilePredicate;
import usi.si.seart.analyzer.printer.NodePrinter;
import usi.si.seart.analyzer.printer.Printer;
import usi.si.seart.analyzer.printer.SExpressionPrinter;
import usi.si.seart.analyzer.printer.SyntaxTreePrinter;
import usi.si.seart.analyzer.query.Queries;
import usi.si.seart.analyzer.traverser.Traverser;
import usi.si.seart.model.code.Boilerplate;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Parser;
import usi.si.seart.treesitter.Tree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Collector;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractAnalyzer implements Analyzer {

    Language language;
    Parser parser;
    Tree tree;

    LocalClone localClone;
    Path path;
    String source;

    Counter lineCounter = new LineCounter();
    Counter codeTokenCounter = new CodeTokenCounter();
    Counter totalTokenCounter;
    Counter characterCounter;

    Hasher contentHasher;
    Hasher syntaxTreeHasher = new SyntaxTreeHasher();

    Predicate<Node> containsError = new ContainsErrorPredicate();
    Predicate<Node> containsNonAscii;

    Predicate<Path> testFilePredicate = new TestFilePredicate() {};

    Printer nodePrinter;
    Printer syntaxTreePrinter = new SyntaxTreePrinter();
    Printer sExpressionPrinter = new SExpressionPrinter();

    Queries<List<Node>> queries = new Queries<>() {
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

    Enumerator<Boilerplate> boilerplateEnumerator = node -> null;

    Traverser<List<Node>> previousCommentTraverser = node -> List.of();

    @SneakyThrows({IOException.class})
    protected AbstractAnalyzer(LocalClone localClone, Path path, Language language) {
        this.language = language;
        this.parser = new Parser(language);
        this.localClone = localClone;
        this.path = path;
        this.source = Files.readString(path);
        this.tree = parser.parseString(source);
        NodeMapper mapper = this::getSourceBytes;
        this.totalTokenCounter = new TokenCounter(mapper);
        this.characterCounter = new CharacterCounter(mapper);
        this.contentHasher = new ContentHasher(mapper);
        this.containsNonAscii = new ContainsNonAsciiPredicate(mapper);
        this.nodePrinter = new NodePrinter(mapper);
    }

    protected final byte[] getSourceBytes() {
        return source.getBytes(Settings.DEFAULT_CHARSET);
    }

    @Override
    public void close() {
        tree.close();
        parser.close();
    }

    @Override
    public final Result analyze() {
        File file = extractFileEntity();
        List<Function> functions = extractFunctionEntities(file);
        return new Analyzer.Result(file, functions);
    }

    private File extractFileEntity() {
        Node fileNode = tree.getRootNode();
        return File.builder()
                .repo(localClone.getGitRepo())
                .path(localClone.relativePathOf(path).toString())
                .content(nodePrinter.print(fileNode))
                .contentHash(contentHasher.hash(fileNode))
                .ast(syntaxTreePrinter.print(fileNode))
                .astHash(syntaxTreeHasher.hash(fileNode))
                .sExpression(sExpressionPrinter.print(fileNode))
                .totalTokens(totalTokenCounter.count(fileNode))
                .codeTokens(codeTokenCounter.count(fileNode))
                .lines(lineCounter.count(fileNode))
                .characters(characterCounter.count(fileNode))
                .containsNonAscii(containsNonAscii.test(fileNode))
                .containsError(containsError.test(fileNode))
                .isTest(testFilePredicate.test(path))
                .build();
    }

    private List<Function> extractFunctionEntities(File file) {
        List<Node> targets = queries.getCallableDeclarations(tree.getRootNode());
        List<Function> functions = new ArrayList<>(targets.size());
        for (Node fnNode: targets) {
            List<Node> comments = previousCommentTraverser.getNodes(fnNode);
            String prefixDoc = comments.stream()
                    .map(node -> nodePrinter.print(node))
                    .collect(Collector.of(
                            () -> new StringJoiner("\n", "", "\n").setEmptyValue(""),
                            StringJoiner::add,
                            StringJoiner::merge,
                            StringJoiner::toString
                    ));
            Function function = Function.builder()
                    .file(file)
                    .repo(localClone.getGitRepo())
                    .content(prefixDoc + nodePrinter.print(fnNode))
                    .contentHash(contentHasher.hash(fnNode))
                    .ast(syntaxTreePrinter.print(fnNode))
                    .astHash(syntaxTreeHasher.hash(fnNode))
                    .sExpression(sExpressionPrinter.print(fnNode))
                    .totalTokens(totalTokenCounter.count(fnNode))
                    .codeTokens(codeTokenCounter.count(fnNode))
                    .lines(lineCounter.count(fnNode))
                    .characters(characterCounter.count(fnNode))
                    .containsNonAscii(containsNonAscii.test(fnNode))
                    .containsError(containsError.test(fnNode))
                    .boilerplateType(boilerplateEnumerator.asEnum(fnNode))
                    .isTest(file.getIsTest())
                    .build();
            functions.add(function);
        }
        return functions;
    }
}
