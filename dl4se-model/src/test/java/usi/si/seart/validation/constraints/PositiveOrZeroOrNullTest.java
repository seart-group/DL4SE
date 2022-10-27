package usi.si.seart.validation.constraints;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

class PositiveOrZeroOrNullTest {

    static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AllArgsConstructor
    private static final class Contract {
        @PositiveOrZeroOrNull
        Long value;
    }

    @ParameterizedTest
    @ValueSource(longs = { -1, -10, -1000, Long.MIN_VALUE })
    void invalidTest(Long value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertFalse(violations.isEmpty());

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = { 0, 1, 10, 1000, Long.MAX_VALUE })
    void validTest(Long value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertTrue(violations.isEmpty());
    }
}
