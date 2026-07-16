package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.inventory.model.Inventory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for managing {@link Inventory} entities.
 * Handles the core business logic and acts as an intermediary between the controller and the repository.
 */
@Service
public class InventoryService {
    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all available inventory items from the system.
     *
     * @return A list containing all {@link Inventory} entities.
     */
    public List<Inventory> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a specific inventory item by its unique identifier.
     *
     * @param id The unique identifier of the inventory item to retrieve.
     * @return An {@link Optional} containing the inventory item if it exists, or empty otherwise.
     */
    public Optional<Inventory> getById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Persists a new inventory item in the system.
     *
     * @param inventory The {@link Inventory} entity containing the data to be saved.
     * @return The saved {@link Inventory} entity with its generated ID.
     */
    public Inventory create(Inventory inventory) {
        return repository.save(inventory);
    }

    /**
     * Updates an existing inventory item with new data.
     *
     * @param id The unique identifier of the inventory item to be updated.
     * @param inventory The {@link Inventory} entity containing the updated information.
     * @return The updated {@link Inventory} entity.
     */
    public Inventory update(UUID id, Inventory inventory) {
        inventory.setId(id);
        return repository.update(inventory);
    }

    /**
     * Removes an inventory item from the system by its ID.
     *
     * @param id The unique identifier of the inventory item to be deleted.
     */
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
