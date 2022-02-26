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
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.utils.NodeUtils;
import usi.si.seart.collection.Tuple;
import usi.si.seart.utils.PathUtils;
import usi.si.seart.utils.StringUtils;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@Slf4j
public class JavaParser extends AbstractParser {

    private static final XmlPrinter astPrinter = new XmlPrinter(true);

    public JavaParser(Language language) {
        super(language);
    }

    @Override
    @SneakyThrows({FileNotFoundException.class})
    public File parse(Path path) throws ParsingException {
        fileBuilder.isTest(PathUtils.isTestFile(path));
        fileBuilder.language(language);

        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(path.toFile());
            fileBuilder.isParsed(true);
            new VoidVisitor().visit(compilationUnit, null);
        } catch (ParseProblemException ex) {
            log.error("Parsing failed for: " + path, ex);
            throw new ParsingException(ex.getMessage(), ex.getCause());
        }

        return buildFileAndFunctions();
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
            functionBuilder.language(language);

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

            functionBuilder.boilerplateType(NodeUtils.getBoilerplateType(declaration));

            functionBuilders.add(functionBuilder);
        }
    }
}
