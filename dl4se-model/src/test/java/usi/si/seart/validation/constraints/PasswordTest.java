package usi.si.seart.validation.constraints;

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

class PasswordTest {

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
        @Password
        String email;
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "password", "IAMSHOUTING", "0123456", "ThisThing", "7ATE9", "aA0", "GG&j8Uw,eeZ53@6CnyD8|J?EX"})
    void invalidTest(String value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = { "ThisIsATest123", "With1SpecialChar!", "G&j8UweeZ3@6nD8|J?X"})
    void validTest(String value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertTrue(violations.isEmpty());
    }
}
