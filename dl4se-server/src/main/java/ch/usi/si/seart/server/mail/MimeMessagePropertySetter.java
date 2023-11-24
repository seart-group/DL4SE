package ch.usi.si.seart.server.mail;

import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;

@FunctionalInterface
public interface MimeMessagePropertySetter {

    void setProperties(MimeMessageHelper mimeMessageHelper) throws MessagingException;
}
