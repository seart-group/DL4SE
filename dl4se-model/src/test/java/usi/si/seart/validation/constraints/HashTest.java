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

class HashTest {

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
        @Hash
        String hash;
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "1f9e046a", // Adler32
            "xa0a9f2a6772942557ab5355d76af442f8f65e01",
            "#a0a9f2a6772942557ab5355d76af442f8f65e01"
    })
    void invalidTest(String value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "65a8e27d8879283831b664bd8b7f0ad4", // MD5
            "0a0a9f2a6772942557ab5355d76af442f8f65e01", // SHA-1
            "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f", // SHA-256
            "374d794a95cdcfd8b35993185fef9ba368f160d8daf432d08ba9f1ed1e5abe6c" +
            "c69291e0fa2fe0006a52570ef18c19def4e617c33ce52ef0a6e5fbe318cb0387" // SHA-512
    })
    void validTest(String value) {
        Set<ConstraintViolation<Contract>> violations = validator.validate(new Contract(value));
        Assertions.assertTrue(violations.isEmpty());
    }
}
