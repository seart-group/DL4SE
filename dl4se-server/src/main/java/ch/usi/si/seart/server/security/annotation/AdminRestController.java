package ch.usi.si.seart.server.security.annotation;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Secured("ROLE_ADMIN")
@RestController
public @interface AdminRestController {
}
