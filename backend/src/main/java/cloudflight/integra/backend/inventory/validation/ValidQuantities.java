package cloudflight.integra.backend.inventory.validation;

import static java.lang.annotation.ElementType.TYPE;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidQuantitiesValidator.class)
public @interface ValidQuantities {

    String message() default "availableQuantity must not exceed totalQuantity";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
