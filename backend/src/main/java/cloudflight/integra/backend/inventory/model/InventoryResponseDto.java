package cloudflight.integra.backend.inventory.model;

public record InventoryResponseDto(Long id, Long venueId, String name, int totalQuantity, int availableQuantity) {}
