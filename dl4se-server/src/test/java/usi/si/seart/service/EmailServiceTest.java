package usi.si.seart.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.mail.internet.MimeMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:application-test.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
class EmailServiceTest {

    @Autowired
    EmailService emailService;

    @TestConfiguration
    static class EmailServiceTestContextConfiguration {

        @Bean
        public ConversionService conversionService() {
            return new DefaultConversionService();
        }

        @Bean
        public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
            return new HandlerMappingIntrospector();
        }
    }

    @RegisterExtension
    static final GreenMailExtension greenMailProxy = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(
                    GreenMailConfiguration.aConfig()
                            .withUser("dabico@dl4se.ch", "dabico", "password1234")
                            .withUser("emadpres@dl4se.ch", "emadpres", "password2345")
            )
            .withPerMethodLifecycle(false);

    @Test
    @SneakyThrows
    void sendVerificationEmailTest() {
        String to = "emadpres@dl4se.ch";
        String link = "https://localhost:8080/api/user/verify?token=1f83b5ff-a4cb-4677-bd7e-01d2e8e90f7f";
        emailService.sendVerificationEmail(to, link);

        MimeMessage[] receivedMessages = greenMailProxy.getReceivedMessages();
        Assertions.assertEquals(1, receivedMessages.length);

        MimeMessage receivedMessage = receivedMessages[0];
        String recipient = receivedMessage.getAllRecipients()[0].toString();
        String body = GreenMailUtil.getBody(receivedMessage);
        String subject = receivedMessage.getSubject();
        Assertions.assertEquals(1, receivedMessage.getAllRecipients().length);
        Assertions.assertEquals(to, recipient);
        Assertions.assertEquals("Complete your DL4SE registration", subject);
        Assertions.assertTrue(body.contains(link));
    }
}