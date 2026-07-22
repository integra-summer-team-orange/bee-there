package cloudflight.integra.backend.inventory.model;

import java.util.UUID;

public record InventoryResponseDto(Long id, UUID venueId, String name, int totalQuantity, int availableQuantity) {}
