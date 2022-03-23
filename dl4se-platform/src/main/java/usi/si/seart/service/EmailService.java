package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

public interface EmailService {

    void sendMessage(String address, String subject, String text);

    @Slf4j
    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class EmailServiceImpl implements EmailService {

        @Value("${spring.mail.username}")
        String sender;

        final JavaMailSender mailSender;

        public void sendMessage(String recipient, String subject, String text) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(text);
            try {
                mailSender.send(message);
            } catch (MailException ex) {
                log.error("Exception occurred while sending email!", ex);
            }
        }
    }
}
