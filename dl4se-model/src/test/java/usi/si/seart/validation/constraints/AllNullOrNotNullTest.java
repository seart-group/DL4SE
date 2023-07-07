package usi.si.seart.validation.constraints;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

class AllNullOrNotNullTest {

    static Validator validator;

    @BeforeAll
    static void setUp() {
        @Cleanup ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        validator = factory.getValidator();
    }

    @AllNullOrNotNull(fields = {"token", "percentage", "contiguous"})
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static final class Contract {
        String token;
        Integer percentage;
        Boolean contiguous;
    }

    @ParameterizedTest
    @CsvSource({ "TOKEN,,", ",1,", ",,false", "TOKEN,1,", ",1,false" })
    void invalidTest(String token, Integer percentage, Boolean contiguous) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(token, percentage, contiguous));
        Assertions.assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({ "TOKEN,1,false", ",," })
    void validTest(String token, Integer percentage, Boolean contiguous) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(token, percentage, contiguous));
        Assertions.assertTrue(violations.isEmpty());
    }
}
