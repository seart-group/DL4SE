package ch.usi.si.seart.server.thymeleaf;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IPostProcessorDialect;
import org.thymeleaf.postprocessor.IPostProcessor;
import org.thymeleaf.postprocessor.PostProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.Set;

public class MinifierDialect extends AbstractDialect implements IPostProcessorDialect {

    public MinifierDialect() {
        super("minifier-dialect");
    }

    @Override
    public int getDialectPostProcessorPrecedence() {
        return 1000;
    }

    @Override
    public Set<IPostProcessor> getPostProcessors() {
        return Set.of(new PostProcessor(TemplateMode.HTML, MinifierHandler.class, 1000));
    }
}
