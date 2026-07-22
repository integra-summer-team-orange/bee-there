package cloudflight.integra.backend.coffeemug;

import cloudflight.integra.backend.coffee.CoffeeService;
import cloudflight.integra.backend.coffeemug.model.CoffeeMug;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CoffeeMugService {
    private final CoffeeMugRepository repository;
    private final CoffeeService coffeeService;

    public CoffeeMugService(CoffeeMugRepository repository, CoffeeService coffeeService) {
        this.repository = repository;
        this.coffeeService = coffeeService;
    }

    public List<CoffeeMug> getAll() {
        return repository.findAll();
    }

    public Optional<CoffeeMug> getById(Long id) {
        return repository.findById(id);
    }

    public CoffeeMug create(CoffeeMug mug) {
        validateCoffeeExists(mug);
        return repository.save(mug);
    }

    public Optional<CoffeeMug> update(Long id, CoffeeMug mug) {
        validateCoffeeExists(mug);
        return repository.findById(id).map(existing -> {
            mug.setId(id);
            return repository.save(mug);
        });
    }

    private void validateCoffeeExists(CoffeeMug mug) {
        if (mug.getCoffee() != null
                && coffeeService.getById(mug.getCoffee().getId()).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Coffee with id %d does not exist".formatted(mug.getCoffee().getId()));
        }
    }

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
