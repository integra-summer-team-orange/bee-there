package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.inventory.model.Inventory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InventoryRepository {
    private final Map<UUID, Inventory> inventories = new ConcurrentHashMap<>();

    public List<Inventory> findAll() {
        return new ArrayList<>(inventories.values());
    }

    public Optional<Inventory> findById(UUID id) {
        return Optional.ofNullable(inventories.get(id));
    }

    public Inventory save(Inventory inventory) {
        if (inventory.getId() == null) {
            inventory.setId(UUID.randomUUID());
        }
        inventories.put(inventory.getId(), inventory);
        return inventory;
    }

    public Inventory update(Inventory inventory) {
        if (!inventories.containsKey(inventory.getId())) {
            throw new EntityNotFoundException("Inventory with id: " + inventory.getId() + " not found!");
        }
        inventories.put(inventory.getId(), inventory);
        return inventory;
    }

    public void deleteById(UUID id) {
        if (!inventories.containsKey(id)) {
            throw new EntityNotFoundException("Inventory with id: " + id + " not found!");
        }
        inventories.remove(id);
    }
}
