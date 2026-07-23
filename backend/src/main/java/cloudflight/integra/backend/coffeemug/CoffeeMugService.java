package cloudflight.integra.backend.coffeemug;

import cloudflight.integra.backend.coffee.CoffeeService;
import cloudflight.integra.backend.coffeemug.model.CoffeeMug;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service that provides business operations for managing coffee mugs.
 */
@Service
public class CoffeeMugService {
    private final CoffeeMugRepository repository;
    private final CoffeeService coffeeService;

    /**
     * Creates a new coffee mug service.
     *
     * @param repository the coffee mug repository
     * @param coffeeService the coffee service
     */
    public CoffeeMugService(CoffeeMugRepository repository, CoffeeService coffeeService) {
        this.repository = repository;
        this.coffeeService = coffeeService;
    }

    /**
     * Retrieves all coffee mugs.
     *
     * @return a list of all coffee mugs
     */
    public List<CoffeeMug> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a coffee mug by its unique identifier.
     *
     * @param id the identifier of the coffee mug to retrieve
     * @return an optional containing the requested coffee mug if found, otherwise an empty optional
     */
    public Optional<CoffeeMug> getById(Long id) {
        return repository.findById(id);
    }

    /**
     * Creates a new coffee mug.
     *
     * @param mug the coffee mug to create
     * @return the created coffee mug
     * @throws ResponseStatusException if the referenced coffee does not exist
     */
    public CoffeeMug create(CoffeeMug mug) {
        validateCoffeeExists(mug);
        return repository.save(mug);
    }

    /**
     * Updates an existing coffee mug.
     *
     * @param id the identifier of the coffee mug to update
     * @param mug the updated coffee mug data
     * @return an optional containing the updated coffee mug if found, otherwise an empty optional
     * @throws ResponseStatusException if the referenced coffee does not exist
     */
    public Optional<CoffeeMug> update(Long id, CoffeeMug mug) {
        validateCoffeeExists(mug);
        return repository.findById(id).map(existing -> {
            mug.setId(id);
            return repository.save(mug);
        });
    }

    /**
     * Deletes a coffee mug by its unique identifier.
     *
     * @param id the identifier of the coffee mug to delete
     * @return {@code true} if the coffee mug was deleted, {@code false} if no coffee mug with the specified identifier exists
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

    private void validateCoffeeExists(CoffeeMug mug) {
        if (mug.getCoffee() != null
                && coffeeService.getById(mug.getCoffee().getId()).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Coffee with id %d does not exist".formatted(mug.getCoffee().getId()));
        }
    }
}
