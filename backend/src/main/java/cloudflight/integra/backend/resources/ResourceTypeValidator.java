package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.ResourceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ResourceTypeValidator implements ConstraintValidator<ValidResourceType, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Arrays.stream(ResourceType.values())
                .anyMatch(enumValue -> enumValue.name().equals(value));
    }
}
