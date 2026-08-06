package cloudflight.integra.backend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserRoleValidator.class)
@Documented
public @interface UserRole {

    String message() default "Invalid type. Allowed values: ADMIN, VENUE_ADMIN, PARTICIPANT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
