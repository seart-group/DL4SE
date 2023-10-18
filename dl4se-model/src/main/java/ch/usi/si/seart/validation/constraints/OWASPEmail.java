package ch.usi.si.seart.validation.constraints;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.Length;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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
 * @see <a href="https://owasp.org/www-community/OWASP_Validation_Regex_Repository">OWASP Validation Regex Repository</a>
 * @see <a href="https://www.debuggex.com/r/zefd3GUITaFfXcVl">Regex Definition</a>
 */
@Documented
@ConstraintComposition(CompositionType.AND)
@NotBlank
@Length(min = 7, max = 256)
@Email(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
@Constraint(validatedBy = { })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface OWASPEmail {

    String message() default "{ch.usi.si.seart.validation.constraints.OWASPEmail.message}";

    Class<?>[] groups() default { };

    Class< ? extends Payload>[] payload() default { };
}
