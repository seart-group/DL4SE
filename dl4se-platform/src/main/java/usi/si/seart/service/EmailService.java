package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public interface EmailService {

    void sendVerificationEmail(String recipient, String value);

    @Slf4j
    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class EmailServiceImpl implements EmailService {

        @NonFinal
        @Value("${spring.mail.username}")
        String sender;

        JavaMailSender mailSender;
        SpringTemplateEngine templateEngine;

        public void sendVerificationEmail(String recipient, String link) {
            MimeMessage message = createMessage(
                    "verification", recipient, "Complete your DL4SE registration", Map.of("link", link)
            );
            mailSender.send(message);
        }

        @SneakyThrows(MessagingException.class)
        private MimeMessage createMessage(
                String template, String recipient, String subject, Map<String, Object> variables
        ) {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            Context context = new Context();
            context.setVariables(variables);

            String html = templateEngine.process(template, context);
            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(html, true);
            return message;
        }
    }
}
