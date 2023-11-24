package ch.usi.si.seart.server.mail;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.Locale;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractMimeMessageTextSetter<T> implements MimeMessagePropertySetter {

    MessageTemplate template;

    TemplateEngine templateEngine;

    T payload;

    @Override
    public void setProperties(MimeMessageHelper mimeMessageHelper) throws MessagingException {
        Context context = asContext(payload);
        String html = templateEngine.process(template.toString(), context);
        mimeMessageHelper.setText(html, true);
    }

    protected final Context defaultContext() {
        return new Context(Locale.ENGLISH);
    }

    protected Context asContext(T ignored) {
        return defaultContext();
    }
}
