package ch.usi.si.seart.server.config;

import ch.usi.si.seart.server.mail.MimeMessagePropertySetter;
import ch.usi.si.seart.server.mail.MimeMessageSenderSetter;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Bean
    public MimeMessagePropertySetter mimeMessageSenderSetter(MailProperties properties) {
        return new MimeMessageSenderSetter(properties.getUsername());
    }
}
