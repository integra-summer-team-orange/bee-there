package cloudflight.integra.backend.inventory.model;

import cloudflight.integra.backend.inventory.validation.ValidQuantities;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

@ValidQuantities
public record InventoryRequestDto(
        @NotNull UUID venueId,
        @NotBlank String name,
        @NotNull @PositiveOrZero Integer totalQuantity,
        @NotNull @PositiveOrZero Integer availableQuantity
) {
}
