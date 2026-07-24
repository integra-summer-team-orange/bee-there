package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.inventory.model.Inventory;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Service class for managing {@link Inventory} entities.
 * Handles the core business logic and acts as an intermediary between the controller and the repository.
 */
@Service
public class InventoryService {
    private final InventoryRepository repository;

    /**
     * Creates a new inventory service.
     *
     * @param repository the inventory repository
     */
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
    public Optional<Inventory> getById(Long id) {
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
     * @throws EntityNotFoundException if no inventory item with the specified identifier exists
     * @return The updated {@link Inventory} entity.
     */
    public Inventory update(Long id, Inventory inventory) {
        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Inventory with id: " + id + " not found!");
        }
        inventory.setId(id);
        return repository.save(inventory);
    }

    /**
     * Removes an inventory item from the system by its ID.
     * @throws EntityNotFoundException if no inventory item with the specified identifier exists
     * @param id The unique identifier of the inventory item to be deleted.
     */
    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Inventory with id: " + id + " not found!");
        }
        repository.deleteById(id);
    }
}
