package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import usi.si.seart.model.task.Task;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public interface EmailService {

    void sendTaskNotificationEmail(Task task);
    void sendVerificationEmail(String recipient, String link);
    void sendPasswordResetEmail(String recipient, String link);

    @Service
    @ConditionalOnProperty(value = "spring.mail.enabled", havingValue = "false")
    class VoidEmailService implements EmailService {

        @Override
        public void sendTaskNotificationEmail(Task task) {
        }

        @Override
        public void sendVerificationEmail(String recipient, String link) {
        }

        @Override
        public void sendPasswordResetEmail(String recipient, String link) {
        }
    }

    @Service
    @ConditionalOnProperty(value = "spring.mail.enabled", havingValue = "true")
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class EmailServiceImpl implements EmailService {

        @NonFinal
        @Value("${spring.mail.username}")
        String sender;

        JavaMailSender mailSender;
        SpringTemplateEngine templateEngine;

        @Override
        public void sendTaskNotificationEmail(Task task) {
            String recipient = task.getUser().getEmail();
            String uuidString = task.getUuid().toString();
            String statusName = task.getStatus().name();
            String subject = String.format("Task [%s]: %s", uuidString, statusName);
            FileSize exportSize = new FileSize(task.getSize());

            Map<String, Object> variables = new HashMap<>();
            variables.put("uuid", uuidString);
            variables.put("status", statusName);
            variables.put("submitted", task.getSubmitted());
            variables.put("started", task.getStarted());
            variables.put("finished", task.getFinished());
            variables.put("results", task.getProcessedResults());
            variables.put("size", exportSize.toString());

            MimeMessage message = createMessage("task_notification", recipient, subject, variables);
            mailSender.send(message);
        }

        @Override
        public void sendVerificationEmail(String recipient, String link) {
            MimeMessage message = createMessage(
                    "verification", recipient, "Complete your DL4SE registration", Map.of("link", link)
            );
            mailSender.send(message);
        }

        @Override
        public void sendPasswordResetEmail(String recipient, String link) {
            MimeMessage message = createMessage(
                    "password_reset", recipient, "Reset your password", Map.of("link", link)
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

        // TODO 03.11.22: We may want to move this somewhere else later
        @AllArgsConstructor
        private static class FileSize {

            private final long bytes;

            private static final double UNIT = 1024.0;

            @Override
            public String toString() {
                // https://stackoverflow.com/a/3758880/17173324
                long absolute = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
                if (absolute < UNIT)
                    return bytes + " B";
                long value = absolute;
                CharacterIterator ci = new StringCharacterIterator("KMGTPE");
                for (int i = 40; i >= 0 && absolute > 0xfffccccccccccccL >> i; i -= 10) {
                    value >>= 10;
                    ci.next();
                }
                value *= Long.signum(bytes);
                return String.format("%.2f %cB", value / UNIT, ci.current());
            }
        }
    }
}
