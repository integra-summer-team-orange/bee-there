package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.inventory.model.Inventory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing inventory entities.
 */
@Repository
public class InventoryRepository {
    private final Map<Long, Inventory> inventories = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all inventory entries.
     *
     * @return a list of all inventory entries
     */
    public List<Inventory> findAll() {
        return new ArrayList<>(inventories.values());
    }

    /**
     * Retrieves an inventory entry by its unique identifier.
     *
     * @param id the identifier of the inventory entry to retrieve
     * @return an optional containing the requested inventory entry if found, otherwise an empty optional
     */
    public Optional<Inventory> findById(Long id) {
        return Optional.ofNullable(inventories.get(id));
    }

    /**
     * Saves an inventory entry.
     *
     * <p>If the inventory entry does not yet have an identifier, a new one is generated.</p>
     *
     * @param inventory the inventory entry to save
     * @return the saved inventory entry
     */
    public Inventory save(Inventory inventory) {
        if (inventory.getId() == null) {
            inventory.setId(idGen.getAndIncrement());
        }
        inventories.put(inventory.getId(), inventory);
        return inventory;
    }

    /**
     * Deletes an inventory entry by its unique identifier.
     *
     * @param id the identifier of the inventory entry to delete
     */
    public void deleteById(Long id) {
        inventories.remove(id);
    }
}
