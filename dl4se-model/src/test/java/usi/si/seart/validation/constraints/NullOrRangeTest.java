package usi.si.seart.validation.constraints;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Stream;

class NullOrRangeTest {

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
        @NullOrRange(min = 1, max = 100)
        Integer percentage;
    }

    @ParameterizedTest
    @MethodSource("invalidIntegerProvider")
    void invalidTest(Integer value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertFalse(violations.isEmpty());
    }

    static Stream<Arguments> invalidIntegerProvider() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(101),
                Arguments.of(Integer.MIN_VALUE),
                Arguments.of(Integer.MAX_VALUE)
        );
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("validIntegerProvider")
    void validTest(Integer value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertTrue(violations.isEmpty());
    }

    static Stream<Arguments> validIntegerProvider() {
        return Stream.of(
                Arguments.of(1),
                Arguments.of(25),
                Arguments.of(50),
                Arguments.of(75),
                Arguments.of(100)
        );
    }
}
