package usi.si.seart.parser;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.XmlPrinter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.utils.NodeUtils;
import usi.si.seart.collection.Tuple;
import usi.si.seart.utils.PathUtils;
import usi.si.seart.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class JavaParser extends AbstractParser {

    private static final XmlPrinter astPrinter = new XmlPrinter(true);

    public JavaParser(Path clonePath) {
        super(clonePath);
    }

    @Override
    @SneakyThrows
    public File parse(Path path) {
        String relativeFilePath = FileSystems.getDefault().getSeparator() + clonePath.relativize(path);
        fileBuilder.path(relativeFilePath);
        fileBuilder.isTest(PathUtils.isTestFile(path));

        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(path.toFile());
            fileBuilder.isParsed(true);
            new VoidVisitor().visit(compilationUnit, null);
        } catch (ParseProblemException ex) {
            log.error("Parsing failed for: " + path, ex);
            fileBuilder.isParsed(false);
            String fileContents = Files.readString(path, StandardCharsets.UTF_8);
            String normalized = StringUtils.normalizeSpace(fileContents);
            fileBuilder.content(fileContents);
            fileBuilder.contentHash(StringUtils.sha256(normalized));
            fileBuilder.lines(fileContents.lines().count());
            fileBuilder.characters(fileContents.chars().count());
        }

        return buildAll();
    }

    private class VoidVisitor extends VoidVisitorAdapter<Object> {

        @Override
        public void visit(CompilationUnit declaration, Object arg) {
            String fileContents = declaration.toString();
            String normalized = StringUtils.normalizeSpace(fileContents);
            fileBuilder.content(fileContents);
            fileBuilder.contentHash(StringUtils.sha256(normalized));

            fileBuilder.ast(astPrinter.output(declaration));
            fileBuilder.astHash(NodeUtils.getAstHash(declaration));

            Tuple<Long, Long> tokensCount = NodeUtils.countTokens(declaration);
            fileBuilder.totalTokens(tokensCount.getLeft());
            fileBuilder.codeTokens(tokensCount.getRight());

            fileBuilder.lines(NodeUtils.countLines(declaration));
            fileBuilder.characters(fileContents.chars().count());

            fileBuilder.containsNonAscii(StringUtils.containsNonAscii(fileContents));

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

            String functionContents = declaration.toString();
            String normalized = StringUtils.normalizeSpace(functionContents);
            functionBuilder.content(functionContents);
            functionBuilder.contentHash(StringUtils.sha256(normalized));

            functionBuilder.ast(astPrinter.output(declaration));
            functionBuilder.astHash(NodeUtils.getAstHash(declaration));

            Tuple<Long, Long> tokensCount = NodeUtils.countTokens(declaration);
            functionBuilder.totalTokens(tokensCount.getLeft());
            functionBuilder.codeTokens(tokensCount.getRight());

            functionBuilder.lines(NodeUtils.countLines(declaration));
            functionBuilder.characters(functionContents.chars().count());

            functionBuilder.containsNonAscii(StringUtils.containsNonAscii(functionContents));

            functionBuilder.isBoilerplate(NodeUtils.isBoilerplate(declaration));

            functionBuilders.add(functionBuilder);
        }
    }
}
