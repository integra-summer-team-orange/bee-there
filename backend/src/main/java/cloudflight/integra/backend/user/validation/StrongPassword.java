package cloudflight.integra.backend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
@Documented
public @interface StrongPassword {

    String message() default "Password must contain at least one uppercase letter, one digit and one symbol !@#$%^&*()";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
