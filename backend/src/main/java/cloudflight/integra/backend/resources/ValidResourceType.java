package cloudflight.integra.backend.resources;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ResourceTypeValidator.class)
@Documented
public @interface ValidResourceType {
    String message() default "Invalid type. Allowed values: INDOOR_SPORT, OUTDOOR_SPORT, BOARDGAME_SOCIAL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
