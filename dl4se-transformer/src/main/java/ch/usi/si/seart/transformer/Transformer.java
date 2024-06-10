package ch.usi.si.seart.transformer;

import ch.usi.si.seart.treesitter.Language;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface Transformer extends UnaryOperator<String> {

    String apply(String source);

    default Language getLanguage() {
        return null;
    }

    static Transformer identity() {
        return string -> string;
    }

    static Transformer chain(Transformer... transformers) {
        return chain(List.of(transformers));
    }

    static Transformer chain(Collection<Transformer> transformers) {
        if (!canChain(transformers))
            throw new IllegalArgumentException("Transformers operating on different languages can not be chained!");
        return source -> {
            Function<String, String> pipeline =  transformers.stream()
                    .map(transformer -> (Function<String,String>) transformer)
                    .reduce(Transformer.identity(), Function::andThen);
            return pipeline.apply(source);
        };
    }

    private static boolean canChain(Collection<Transformer> transformers) {
        return 2 > transformers.stream()
                .map(Transformer::getLanguage)
                .filter(Objects::nonNull)
                .distinct()
                .count();
    }
}
