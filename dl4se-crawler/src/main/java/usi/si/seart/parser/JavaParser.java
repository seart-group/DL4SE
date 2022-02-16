package usi.si.seart.parser;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.utils.NodeUtils;
import usi.si.seart.utils.PathUtils;
import usi.si.seart.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class JavaParser extends AbstractParser {

    public JavaParser(Path clonePath) {
        super(clonePath);
    }

    @Override
    @SneakyThrows
    public File parse(Path path) {
        String relativeFilePath = FileSystems.getDefault().getSeparator() + clonePath.relativize(path);
        this.file.setPath(relativeFilePath);
        this.file.setIsTest(PathUtils.isTestFile(path));

        try {
            CompilationUnit compilationUnit = StaticJavaParser.parse(path.toFile());
            this.file.setIsParsed(true);
            new VoidVisitor().visit(compilationUnit, null);
        } catch (ParseProblemException ex) {
            log.error("Parsing failed for: " + path, ex);
            this.file.setIsParsed(false);
            String fileContents = Files.readString(path, StandardCharsets.UTF_8);
            this.file.setContent(fileContents);
            // TODO: 15.02.22 Normalize code before calling the hashing alg?
            this.file.setContentHash(StringUtils.sha256(fileContents));
            this.file.setLines(fileContents.lines().count());
            this.file.setCharacters(fileContents.chars().count());
        }

        return this.file;
    }

    private class VoidVisitor extends VoidVisitorAdapter<Object> {

        @Override
        public void visit(CompilationUnit declaration, Object arg) {
            String fileContents = declaration.toString();
            String normalized = StringUtils.normalizeSpace(fileContents);
            JavaParser.this.file.setContent(fileContents);
            JavaParser.this.file.setContentHash(StringUtils.sha256(fileContents));

            // ast and ast hash

            JavaParser.this.file.setTokens(NodeUtils.countTokens(declaration));
            JavaParser.this.file.setLines(NodeUtils.countLines(declaration));
            JavaParser.this.file.setCharacters(fileContents.chars().count());

            JavaParser.this.file.setContainsNonAscii(
                    StringUtils.containsNonAscii(fileContents)
            );

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
            Function function = Function.builder().build();

            String functionContents = declaration.toString();
            function.setContent(functionContents);
            function.setContentHash(StringUtils.sha256(functionContents));

            // ast and ast hash

            function.setTokens(NodeUtils.countTokens(declaration));
            function.setLines(NodeUtils.countLines(declaration));
            function.setCharacters(functionContents.chars().count());

            function.setContainsNonAscii(
                    StringUtils.containsNonAscii(functionContents)
            );

            function.setIsTest(JavaParser.this.file.getIsTest());
            // TODO: 15.02.22 Other determiners for test functions?

            function.setIsBoilerplate(NodeUtils.isBoilerplate(declaration));
            // TODO: 15.02.22 Other determiners for boilerplate functions?

            function.setFile(JavaParser.this.file);
            JavaParser.this.file.getFunctions().add(function);
        }
    }
}
