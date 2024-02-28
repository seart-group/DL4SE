package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.model.task.Task_;
import ch.usi.si.seart.model.user.token.PasswordResetToken;
import ch.usi.si.seart.model.user.token.VerificationToken;
import ch.usi.si.seart.server.hateoas.URLGenerator;
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

import java.net.URL;

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

        URLGenerator<VerificationToken> verificationURLGenerator;
        URLGenerator<PasswordResetToken> passwordResetURLGenerator;

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
            URL url = verificationURLGenerator.generate(token);
            MimeMessagePropertySetter messageRecipientSetter = new MimeMessageRecipientSetter(email);
            MimeMessagePropertySetter messageSubjectSetter = new MimeMessageSubjectSetter("Complete your registration");
            MimeMessagePropertySetter messageTextSetter = new MimeMessageURLTextSetter(template, url);
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
            URL url = passwordResetURLGenerator.generate(token);
            MimeMessagePropertySetter messageRecipientSetter = new MimeMessageRecipientSetter(email);
            MimeMessagePropertySetter messageSubjectSetter = new MimeMessageSubjectSetter("Reset your password");
            MimeMessagePropertySetter messageTextSetter = new MimeMessageURLTextSetter(template, url);
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
                context.setVariable(Task_.UUID, payload.getUuid());
                context.setVariable(Task_.STATUS, payload.getStatus());
                context.setVariable(Task_.SUBMITTED, payload.getSubmitted());
                context.setVariable(Task_.STARTED, payload.getStarted());
                context.setVariable(Task_.FINISHED, payload.getFinished());
                context.setVariable(Task_.PROCESSED_RESULTS, payload.getProcessedResults());
                context.setVariable(Task_.SIZE, new ReadableFileSize(payload.getSize()));
                return context;
            }
        }

        private final class MimeMessageURLTextSetter extends AbstractMimeMessageTextSetter<URL> {

            private MimeMessageURLTextSetter(MessageTemplate template, URL payload) {
                super(template, templateEngine, payload);
            }

            @Override
            protected Context asContext(URL payload) {
                Context context = super.defaultContext();
                context.setVariable("link", payload);
                return context;
            }
        }
    }
}
