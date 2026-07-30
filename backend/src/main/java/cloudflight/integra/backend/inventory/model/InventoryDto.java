package cloudflight.integra.backend.inventory.model;

import cloudflight.integra.backend.inventory.validation.ValidQuantities;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@ValidQuantities
public record InventoryDto(
        Long id,
        @NotNull Long venueId,
        @NotBlank String name,
        @NotNull @PositiveOrZero Integer totalQuantity,
        @NotNull @PositiveOrZero Integer availableQuantity) {}
