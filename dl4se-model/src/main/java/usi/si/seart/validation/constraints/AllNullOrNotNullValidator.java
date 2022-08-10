package usi.si.seart.validation.constraints;

import lombok.SneakyThrows;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class AllNullOrNotNullValidator implements ConstraintValidator<AllNullOrNotNull, Object> {

    String[] fieldNames;

    @Override
    public void initialize(AllNullOrNotNull annotation) {
        fieldNames = annotation.fields();
    }

    @Override
    @SneakyThrows
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null || fieldNames.length < 2) return true;
        boolean result = true;
        for (int i = 0; i < fieldNames.length - 1; i++) {
            String fieldNameOne = fieldNames[i];
            String fieldNameTwo = fieldNames[i + 1];
            Field fieldOne = value.getClass().getDeclaredField(fieldNameOne);
            Field fieldTwo = value.getClass().getDeclaredField(fieldNameTwo);
            fieldOne.setAccessible(true);
            fieldTwo.setAccessible(true);
            Object fieldOneValue = fieldOne.get(value);
            Object fieldTwoValue = fieldTwo.get(value);
            boolean bothNull = fieldOneValue == null && fieldTwoValue == null;
            boolean noneNull = fieldOneValue != null && fieldTwoValue != null;
            result &= (bothNull || noneNull);
        }
        return result;
    }
}
