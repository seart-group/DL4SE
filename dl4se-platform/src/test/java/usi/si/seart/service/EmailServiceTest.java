package usi.si.seart.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:application-test.properties")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @RegisterExtension
    private static final GreenMailExtension greenMailProxy = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(
                    GreenMailConfiguration.aConfig()
                            .withUser("dabico@dl4se.ch", "dabico", "password1234")
                            .withUser("emadpres@dl4se.ch", "emadpres", "password2345")
            )
            .withPerMethodLifecycle(false);

    @Test
    @SneakyThrows
    void sendMessageTest() {
        String recipient = "emadpres@dl4se.ch";
        String subject = "DL4SE Mail Test";
        String message = "This is a test for the Java Mail Service, using GreenMail!";
        emailService.sendMessage(recipient, subject, message);

        MimeMessage[] receivedMessages = greenMailProxy.getReceivedMessages();
        Assertions.assertEquals(1, receivedMessages.length);

        MimeMessage receivedMessage = receivedMessages[0];
        Assertions.assertEquals(1, receivedMessage.getAllRecipients().length);
        Assertions.assertEquals(recipient, receivedMessage.getAllRecipients()[0].toString());
        Assertions.assertEquals(subject, receivedMessage.getSubject());
        Assertions.assertEquals(message, GreenMailUtil.getBody(receivedMessage));
    }
}