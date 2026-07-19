package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.inventory.model.Inventory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InventoryRepository {
    private final Map<Long, Inventory> inventories = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Inventory> findAll() {
        return new ArrayList<>(inventories.values());
    }

    public Optional<Inventory> findById(Long id) {
        return Optional.ofNullable(inventories.get(id));
    }

    public Inventory save(Inventory inventory) {
        if (inventory.getId() == null) {
            inventory.setId(idGen.getAndIncrement());
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

    public void deleteById(Long id) {
        if (!inventories.containsKey(id)) {
            throw new EntityNotFoundException("Inventory with id: " + id + " not found!");
        }
        inventories.remove(id);
    }
}
