package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.model.user.token.PasswordResetToken;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.model.user.token.VerificationToken;
import ch.usi.si.seart.server.hateoas.LinkGenerator;
import ch.usi.si.seart.server.mail.AbstractMimeMessageSubjectSetter;
import ch.usi.si.seart.server.mail.AbstractMimeMessageTextSetter;
import ch.usi.si.seart.server.mail.MessageTemplate;
import ch.usi.si.seart.server.mail.MimeMessagePreparatorPipeline;
import ch.usi.si.seart.server.mail.MimeMessagePropertySetter;
import ch.usi.si.seart.server.mail.MimeMessageRecipientSetter;
import ch.usi.si.seart.server.mail.MimeMessageSubjectSetter;
import ch.usi.si.seart.util.unit.ReadableFileSize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public interface EmailService {

    void sendTaskNotificationEmail(Task task);
    void sendVerificationEmail(VerificationToken token);
    void sendPasswordResetEmail(PasswordResetToken token);

    @Service
    @ConditionalOnProperty(value = "spring.mail.enabled", havingValue = "false")
    class VoidEmailService implements EmailService {

        @Override
        public void sendTaskNotificationEmail(Task task) {
        }

        @Override
        public void sendVerificationEmail(VerificationToken token) {
        }

        @Override
        public void sendPasswordResetEmail(PasswordResetToken token) {
        }
    }

    @Async
    @Service
    @ConditionalOnProperty(value = "spring.mail.enabled", havingValue = "true")
    @AllArgsConstructor(onConstructor_ = @Autowired)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class AsyncEmailService implements EmailService {

        JavaMailSender mailSender;

        TemplateEngine templateEngine;
        
        MimeMessagePropertySetter messageSenderSetter;

        LinkGenerator<Token> verificationLinkGenerator;
        LinkGenerator<Token> passwordResetLinkGenerator;

        @Override
        public void sendTaskNotificationEmail(Task task) {
            String email = task.getUser().getEmail();
            MimeMessagePropertySetter messageRecipientSetter = new MimeMessageRecipientSetter(email);
            MimeMessagePropertySetter messageSubjectSetter = new MimeMessageTaskSubjectSetter(task);
            MimeMessagePropertySetter messageTextSetter = new MimeMessageTaskTextSetter(task);
            MimeMessagePreparator preparator = new MimeMessagePreparatorPipeline(
                    messageSenderSetter,
                    messageRecipientSetter,
                    messageSubjectSetter,
                    messageTextSetter
            );
            mailSender.send(preparator);
        }

        @Override
        public void sendVerificationEmail(VerificationToken token) {
            String email = token.getUser().getEmail();
            MessageTemplate template = MessageTemplate.VERIFICATION;
            String link = verificationLinkGenerator.generate(token);
            MimeMessagePropertySetter messageRecipientSetter = new MimeMessageRecipientSetter(email);
            MimeMessagePropertySetter messageSubjectSetter = new MimeMessageSubjectSetter("Complete your registration");
            MimeMessagePropertySetter messageTextSetter = new MimeMessageLinkTextSetter(template, link);
            MimeMessagePreparator preparator = new MimeMessagePreparatorPipeline(
                    messageSenderSetter,
                    messageRecipientSetter,
                    messageSubjectSetter,
                    messageTextSetter
            );
            mailSender.send(preparator);
        }

        @Override
        public void sendPasswordResetEmail(PasswordResetToken token) {
            String email = token.getUser().getEmail();
            MessageTemplate template = MessageTemplate.PASSWORD_RESET;
            String link = passwordResetLinkGenerator.generate(token);
            MimeMessagePropertySetter messageRecipientSetter = new MimeMessageRecipientSetter(email);
            MimeMessagePropertySetter messageSubjectSetter = new MimeMessageSubjectSetter("Reset your password");
            MimeMessagePropertySetter messageTextSetter = new MimeMessageLinkTextSetter(template, link);
            MimeMessagePreparator preparator = new MimeMessagePreparatorPipeline(
                    messageSenderSetter,
                    messageRecipientSetter,
                    messageSubjectSetter,
                    messageTextSetter
            );
            mailSender.send(preparator);
        }

        private static final class MimeMessageTaskSubjectSetter extends AbstractMimeMessageSubjectSetter<Task> {

            private MimeMessageTaskSubjectSetter(Task payload) {
                super(payload);
            }

            @Override
            protected String asSubject(Task payload) {
                return String.format("Task [%s]: %s", payload.getUuid(), payload.getStatus());
            }
        }

        private final class MimeMessageTaskTextSetter extends AbstractMimeMessageTextSetter<Task> {

            private MimeMessageTaskTextSetter(Task payload) {
                super(MessageTemplate.TASK_NOTIFICATION, templateEngine, payload);
            }

            @Override
            protected Context asContext(Task payload) {
                Context context = super.defaultContext();
                context.setVariable("uuid", payload.getUuid());
                context.setVariable("status", payload.getStatus());
                context.setVariable("submitted", payload.getSubmitted());
                context.setVariable("started", payload.getStarted());
                context.setVariable("finished", payload.getFinished());
                context.setVariable("results", payload.getProcessedResults());
                context.setVariable("size", new ReadableFileSize(payload.getSize()));
                return context;
            }
        }

        private final class MimeMessageLinkTextSetter extends AbstractMimeMessageTextSetter<String> {

            private MimeMessageLinkTextSetter(MessageTemplate template, String payload) {
                super(template, templateEngine, payload);
            }

            @Override
            protected Context asContext(String payload) {
                Context context = super.defaultContext();
                context.setVariable("link", payload);
                return context;
            }
        }
    }
}
