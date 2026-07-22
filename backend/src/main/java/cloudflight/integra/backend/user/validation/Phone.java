package cloudflight.integra.backend.user.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface Phone {

    String message() default "Phone must be formed only with numbers and maybe starting with a +";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
