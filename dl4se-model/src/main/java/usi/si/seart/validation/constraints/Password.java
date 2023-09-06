package usi.si.seart.validation.constraints;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author dabico
 * @see <a href="https://www.debuggex.com/r/skKbz_cXoS8B_dsn">Password Regex Definition</a>
 */
@Documented
@ConstraintComposition(CompositionType.AND)
@NotBlank
@Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d).{6,20}$")
@Constraint(validatedBy = { })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface Password {

    @OverridesAttribute(constraint = Pattern.class, name = "regexp")
    String regexp() default "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d).{6,20}$";

    String message() default "{usi.si.seart.validation.constraints.Password.message}";

    Class<?>[] groups() default { };

    Class< ? extends Payload>[] payload() default { };
}
