package ch.usi.si.seart.server.mail;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractMimeMessageSubjectSetter<T> implements MimeMessagePropertySetter {

    T payload;

    @Override
    public void setProperties(MimeMessageHelper mimeMessageHelper) throws MessagingException {
        String subject = asSubject(payload);
        mimeMessageHelper.setSubject(subject);
    }

    protected String asSubject(T payload) {
        return payload.toString();
    }
}
