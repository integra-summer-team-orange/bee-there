package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.inventory.model.Inventory;
import cloudflight.integra.backend.inventory.model.InventoryRequestDto;
import cloudflight.integra.backend.inventory.model.InventoryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for converting between {@link Inventory} entities and their respective Data Transfer Objects (DTOs).
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper {

    /**
     * Converts a request DTO into an {@link Inventory} entity.
     * The ID is managed internally by the application and is ignored during this mapping.
     *
     * @param inventoryRequestDto The DTO containing the incoming data for creating or updating an inventory item.
     * @return The resulting {@link Inventory} entity.
     */
    @Mapping(target = "id", ignore = true)
    Inventory toEntity(InventoryRequestDto inventoryRequestDto);

    /**
     * Converts an {@link Inventory} entity into a response DTO to be sent to the client.
     *
     * @param inventory The entity to be mapped.
     * @return The corresponding {@link InventoryResponseDto} containing the formatted data.
     */
    InventoryResponseDto toResponseDto(Inventory inventory);
}
