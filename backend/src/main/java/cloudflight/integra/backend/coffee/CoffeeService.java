package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.Coffee;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CoffeeService {
    private final CoffeeRepository repository;

    public CoffeeService(CoffeeRepository repository) {
        this.repository = repository;
    }

    public List<Coffee> getAll() {
        return repository.findAll();
    }

    public Optional<Coffee> getById(Long id) {
        return repository.findById(id);
    }

    public Coffee create(Coffee coffee) {
        return repository.save(coffee);
    }

    public Optional<Coffee> update(Long id, Coffee coffee) {
        return repository.findById(id).map(existing -> {
            coffee.setId(id);
            return repository.save(coffee);
        });
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
