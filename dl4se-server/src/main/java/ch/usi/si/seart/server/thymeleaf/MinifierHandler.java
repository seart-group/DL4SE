package ch.usi.si.seart.server.thymeleaf;

import org.thymeleaf.engine.AbstractTemplateHandler;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IText;
import org.thymeleaf.util.StringUtils;

public class MinifierHandler extends AbstractTemplateHandler {

    @Override
    public void handleComment(IComment comment) {
    }

    @Override
    public void handleText(IText text) {
        String content = text.getText();
        if (StringUtils.isEmptyOrWhitespace(content)) return;
        super.handleText(text);
    }
}
