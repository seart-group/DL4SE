package ch.usi.si.seart.server.mail;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MimeMessagePreparatorPipeline implements MimeMessagePreparator {

    Collection<MimeMessagePropertySetter> propertySetters;

    public MimeMessagePreparatorPipeline(MimeMessagePropertySetter... propertySetters) {
        this(List.of(propertySetters));
    }

    @Override
    public void prepare(MimeMessage mimeMessage) throws Exception {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        for (MimeMessagePropertySetter propertySetter: propertySetters) {
            propertySetter.setProperties(mimeMessageHelper);
        }
    }
}
