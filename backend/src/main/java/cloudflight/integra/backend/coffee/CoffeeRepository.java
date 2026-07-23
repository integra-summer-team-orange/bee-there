package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.Coffee;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing coffee entities.
 */
@Repository
public class CoffeeRepository {
    private final Map<Long, Coffee> coffees = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all coffees.
     *
     * @return a list of all coffees
     */
    public List<Coffee> findAll() {
        return new ArrayList<>(coffees.values());
    }

    /**
     * Retrieves a coffee by its unique identifier.
     *
     * @param id the identifier of the coffee to retrieve
     * @return an optional containing the requested coffee if found, otherwise an empty optional
     */
    public Optional<Coffee> findById(Long id) {
        return Optional.ofNullable(coffees.get(id));
    }

    /**
     * Saves a coffee.
     *
     * <p>If the coffee does not yet have an identifier, a new one is generated.</p>
     *
     * @param coffee the coffee to save
     * @return the saved coffee
     */
    public Coffee save(Coffee coffee) {
        if (coffee.getId() == null) {
            coffee.setId(idGen.getAndIncrement());
        }
        coffees.put(coffee.getId(), coffee);
        return coffee;
    }

    /**
     * Deletes a coffee by its unique identifier.
     *
     * @param id the identifier of the coffee to delete
     */
    public void deleteById(Long id) {
        coffees.remove(id);
    }
}
