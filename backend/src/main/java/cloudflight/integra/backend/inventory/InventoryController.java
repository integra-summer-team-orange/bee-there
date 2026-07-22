package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.inventory.model.InventoryRequestDto;
import cloudflight.integra.backend.inventory.model.InventoryResponseDto;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST Controller for managing inventory-related API endpoints.
 * Handles incoming HTTP requests for creating, reading, updating, and deleting inventory items.
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService service;
    private final InventoryMapper mapper;

    public InventoryController(InventoryService service, InventoryMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves a list of all inventory items present in the system.
     *
     * @return A {@link ResponseEntity} containing a list of {@link InventoryResponseDto} with a 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponseDto>> getAll() {
        return ResponseEntity.ok(
                service.getAll().stream().map(mapper::toResponseDto).toList());
    }

    /**
     * Retrieves the details of a specific inventory item based on its ID.
     *
     * @param id The unique identifier of the requested inventory item.
     * @return A {@link ResponseEntity} containing the {@link InventoryResponseDto} with a 200 OK status.
     * @throws EntityNotFoundException if no inventory item is found matching the provided ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id)
                .map(mapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Inventory with id: " + id + " not found!")));
    }

    /**
     * Creates a new inventory item based on the provided request payload.
     *
     * @param dto The validated {@link InventoryRequestDto} containing the inventory details.
     * @return A {@link ResponseEntity} containing the created {@link InventoryResponseDto} with a 201 CREATED
     *         status and a Location header.
     */
    @PostMapping
    public ResponseEntity<InventoryResponseDto> create(@RequestBody @Valid InventoryRequestDto dto) {
        InventoryResponseDto createdInventory = mapper.toResponseDto(service.create(mapper.toEntity(dto)));
        URI location = UriComponentsBuilder.fromPath("/api/inventory/{id}")
                .buildAndExpand(createdInventory.id())
                .toUri();
        return ResponseEntity.created(location).body(createdInventory);
    }

    /**
     * Updates an existing inventory item entirely with the provided payload.
     *
     * @param id The unique identifier of the inventory item to be updated.
     * @param dto The validated {@link InventoryRequestDto} containing the new data.
     * @return A {@link ResponseEntity} containing the updated {@link InventoryResponseDto} with a 200 OK status.
     * @throws EntityNotFoundException if no inventory item is found matching the provided ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDto> update(
            @PathVariable Long id, @RequestBody @Valid InventoryRequestDto dto) {
        return ResponseEntity.ok(mapper.toResponseDto(service.update(id, mapper.toEntity(dto))));
    }

    /**
     * Deletes a specific inventory item from the system by its ID.
     *
     * @param id The unique identifier of the inventory item to be removed.
     * @return An empty {@link ResponseEntity} with a 204 NO CONTENT status.
     * @throws EntityNotFoundException if no inventory item is found matching the provided ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
