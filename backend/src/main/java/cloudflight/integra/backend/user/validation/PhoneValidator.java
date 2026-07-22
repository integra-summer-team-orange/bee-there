package cloudflight.integra.backend.user.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return false;
        }

        // it might begin with + and has only numbers, between 4-16
        return phone.matches("^\\+?\\d{4,16}$");
    }
}
