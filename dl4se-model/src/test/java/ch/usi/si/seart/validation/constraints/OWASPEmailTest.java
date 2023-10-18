package ch.usi.si.seart.validation.constraints;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

class OWASPEmailTest {

    static Validator validator;

    @BeforeAll
    static void setUp() {
        @Cleanup ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        validator = factory.getValidator();
    }

    @AllArgsConstructor
    private static final class Contract {
        @OWASPEmail
        String email;
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "username", "username@domain", "@domain.xy", "a@b.xy", ".username@domain.xy", "username.@domain.xy", "username@domain.xy." })
    void invalidTest(String value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "dabico@usi.ch", "ozren.dabic@usi.ch" })
    void validTest(String value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertTrue(violations.isEmpty());
    }
}
