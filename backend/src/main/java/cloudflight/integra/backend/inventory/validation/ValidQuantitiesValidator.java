package cloudflight.integra.backend.inventory.validation;

import cloudflight.integra.backend.inventory.model.InventoryDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidQuantitiesValidator implements ConstraintValidator<ValidQuantities, InventoryDto> {

    @Override
    public boolean isValid(InventoryDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.totalQuantity() == null || dto.availableQuantity() == null) {
            return true;
        }
        return dto.availableQuantity() <= dto.totalQuantity();
    }
}
