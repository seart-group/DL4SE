package ch.usi.si.seart.server.mail;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MimeMessageRecipientSetter implements MimeMessagePropertySetter {

    String recipient;

    @Override
    public void setProperties(MimeMessageHelper mimeMessageHelper) throws MessagingException {
        mimeMessageHelper.setTo(recipient);
    }
}
