package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.transformer.Transformer;
import ch.usi.si.seart.transformer.remover.JavaBlockCommentRemover;
import ch.usi.si.seart.transformer.remover.JavaDocumentationCommentRemover;
import ch.usi.si.seart.transformer.remover.JavaLineCommentRemover;
import ch.usi.si.seart.transformer.remover.PythonDocstringRemover;
import ch.usi.si.seart.transformer.remover.PythonLineCommentRemover;
import ch.usi.si.seart.transformer.wrapper.JavaDummyClassWrapper;
import ch.usi.si.seart.treesitter.Language;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TaskToTransformerConverter extends TaskConverter<Transformer> {

    private static final Map<String, Language> LANGUAGE_NAME_LOOKUP = Stream.of(Language.values())
            .collect(Collectors.toUnmodifiableMap(
                    Language::toString,
                    Function.identity()
            ));

    @Override
    protected void validate(Task source) {
        super.validate(source);
        JsonNode query = source.getQuery();
        Assert.isTrue(query.has("granularity"), "Query must have 'granularity' defined!");
        Assert.isTrue(query.has("language_name"), "Query must have 'language_name' defined!");
    }

    @Override
    protected Transformer convertInternal(Task source) {
        JsonNode query = source.getQuery();
        JsonNode processing = source.getProcessing();
        return convert(query, processing);
    }

    private Transformer convert(JsonNode query, JsonNode processing) {
        String languageName = query.get("language_name").asText();
        String granularity = query.get("granularity").asText();
        return convert(processing, languageName, granularity);
    }

    private Transformer convert(JsonNode processing, String languageName, String granularity) {
        Language language = LANGUAGE_NAME_LOOKUP.get(languageName);
        return convert(processing, language, granularity);
    }

    private Transformer convert(JsonNode processing, Language language, String granularity) {
        List<Transformer> transformers = new ArrayList<>();

        // Inner comment removers
        Optional.ofNullable(processing.get("remove_regular_comments"))
                .map(JsonNode::asBoolean)
                .filter(Boolean::booleanValue)
                .map(ignored -> getInnerCommentRemover(language))
                .ifPresent(transformers::add);

        // Documentation comment removers
        Optional.ofNullable(processing.get("remove_documentation_comments"))
                .map(JsonNode::asBoolean)
                .filter(Boolean::booleanValue)
                .map(ignored -> getDocumentationCommentRemover(language))
                .ifPresent(transformers::add);

        // Special wrap and unwrap handling for Java functions
        if (Language.JAVA.equals(language) && "function".equals(granularity)) {
            transformers.add(0, new JavaDummyClassWrapper());
            transformers.add(source -> {
                int lowerIndex = source.indexOf('\n') + 1;
                int upperIndex = source.lastIndexOf('\n');
                return source.substring(lowerIndex, upperIndex);
            });
        }

        return Transformer.chain(transformers);
    }

    private static Transformer getInnerCommentRemover(Language language) {
        switch (language) {
            case JAVA:
                return Transformer.chain(
                        new JavaLineCommentRemover(),
                        new JavaBlockCommentRemover()
                );
            case PYTHON:
                return new PythonLineCommentRemover();
            default:
                return Transformer.identity();
        }
    }

    private static Transformer getDocumentationCommentRemover(Language language) {
        switch (language) {
            case JAVA:
                return new JavaDocumentationCommentRemover();
            case PYTHON:
                return new PythonDocstringRemover();
            default:
                return Transformer.identity();
        }
    }
}
