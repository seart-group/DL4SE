package usi.si.seart.converter;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.printer.XmlPrinter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.function.CodeProcessingPipeline;
import usi.si.seart.model.code.Code;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
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
import usi.si.seart.wrapper.code.Abstracted;
import usi.si.seart.wrapper.code.AbstractedFile;
import usi.si.seart.wrapper.code.AbstractedFunction;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

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

    private static final Predicate<Comment> IS_ANY_COMMENT = comment -> true;
    private static final Predicate<Comment> IS_LINE_COMMENT = Comment::isLineComment;
    private static final Predicate<Comment> IS_BLOCK_COMMENT = Comment::isBlockComment;
    private static final Predicate<Comment> IS_JAVADOC_COMMENT = Comment::isJavadocComment;

    private CodeProcessingPipeline convert(
            CodeProcessing codeProcessing, boolean includeAst, java.util.function.Function<String, Node> parser
    ) {
        CodeProcessingPipeline pipeline = new CodeProcessingPipeline();

        if (!includeAst) {
            pipeline.add(code -> {
                code.setAst("");
                return code;
            });
        }

        boolean removeDocstring = codeProcessing.getRemoveDocstring();
        boolean removeInnerComments = codeProcessing.getRemoveInnerComments();
        if (removeDocstring || removeInnerComments) {
            pipeline.add(code -> {
                try {
                    Node node = parser.apply(code.getContent());
                    List<Comment> comments = node.getAllContainedComments();
                    node.getComment().ifPresent(comments::add);
                    Predicate<Comment> commentPredicate;
                    if (removeDocstring && removeInnerComments) {
                        commentPredicate = IS_ANY_COMMENT;
                    } else if (removeDocstring) {
                        commentPredicate = IS_JAVADOC_COMMENT;
                    } else {
                        commentPredicate = IS_LINE_COMMENT.or(IS_BLOCK_COMMENT);
                    }
                    comments.stream().filter(commentPredicate).forEach(Comment::remove);
                    code.setContent(node.toString());
                    if (includeAst) {
                        code.setAst(astPrinter.output(node));
                    }
                } catch (ParseProblemException | StackOverflowError ignore) {}
                return code;
            });
        }

        boolean abstractCode = codeProcessing.getAbstractCode();
        if (abstractCode) {
            pipeline.add(code -> {
                try {
                    // Perform abstraction
                    String sourceCode = code.getContent();
                    String cleanedCode = Abstractor.cleanCode(sourceCode);
                    Set<String> idioms = new TreeSet<>(codeProcessing.getAbstractIdioms());
                    Parser absParser = new Parser();
                    absParser.parse(cleanedCode, parser);
                    Tokenizer tokenizer = new Tokenizer(absParser, idioms);
                    Node node = parser.apply(tokenizer.tokenize(sourceCode));
                    code.setContent(node.toString());
                    if (includeAst) {
                        code.setAst(astPrinter.output(node));
                    }

                    // Convert result to wrapper object that includes the mappings
                    Abstracted abstracted;
                    if (code instanceof File) {
                        abstracted = AbstractedFile.from((File) code);
                    } else if (code instanceof Function) {
                        abstracted = AbstractedFunction.from((Function) code);
                    } else {
                        throw new UnsupportedOperationException(
                                "Abstraction operation not implemented for granularity: " + code.getClass().getName()
                        );
                    }
                    abstracted.setMappings(tokenizer.export());
                    code = (Code) abstracted;
                } catch (ParseProblemException | StackOverflowError ignore) {}
                return code;
            });
        }

        // TODO 19.07.22: Masking (make sure consecutive choices are "flattened")

        return pipeline;
    }
}
