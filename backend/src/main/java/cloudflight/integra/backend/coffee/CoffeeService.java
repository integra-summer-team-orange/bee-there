package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.Coffee;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Service that provides business operations for managing coffees.
 */
@Service
public class CoffeeService {
    private final CoffeeRepository repository;

    /**
     * Creates a new coffee service.
     *
     * @param repository the coffee repository
     */
    public CoffeeService(CoffeeRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all coffees.
     *
     * @return a list of all coffees
     */
    public List<Coffee> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a coffee by its unique identifier.
     *
     * @param id the identifier of the coffee to retrieve
     * @return an optional containing the requested coffee if found, otherwise an empty optional
     */
    public Optional<Coffee> getById(Long id) {
        return repository.findById(id);
    }

    /**
     * Creates a new coffee.
     *
     * @param coffee the coffee to create
     * @return the created coffee
     */
    public Coffee create(Coffee coffee) {
        return repository.save(coffee);
    }

    /**
     * Updates an existing coffee.
     *
     * @param id the identifier of the coffee to update
     * @param coffee the updated coffee data
     * @return an optional containing the updated coffee if found, otherwise an empty optional
     */
    public Optional<Coffee> update(Long id, Coffee coffee) {
        return repository.findById(id).map(existing -> {
            coffee.setId(id);
            return repository.save(coffee);
        });
    }

    /**
     * Deletes a coffee by its unique identifier.
     *
     * @param id the identifier of the coffee to delete
     * @return {@code true} if the coffee was deleted, {@code false} if no coffee with the specified identifier exists
     */
    public boolean delete(Long id) {
        return repository
                .findById(id)
                .map(existing -> {
                    repository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
