package cloudflight.integra.backend.user.validation;

import cloudflight.integra.backend.user.model.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class UserRoleValidator implements ConstraintValidator<UserRole, String> {
    @Override
    public boolean isValid(String role, ConstraintValidatorContext context) {
        if (role == null) {
            return false;
        }
        return Arrays.stream(Role.values())
                .anyMatch(enumValue -> enumValue.name().equals(role));
    }
}
