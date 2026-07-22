package cloudflight.integra.backend.inventory.validation;

import cloudflight.integra.backend.inventory.model.InventoryRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidQuantitiesValidator implements ConstraintValidator<ValidQuantities, InventoryRequestDto> {

    @Override
    public boolean isValid(InventoryRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.totalQuantity() == null || dto.availableQuantity() == null) {
            return true;
        }
        return dto.availableQuantity() <= dto.totalQuantity();
    }
}
