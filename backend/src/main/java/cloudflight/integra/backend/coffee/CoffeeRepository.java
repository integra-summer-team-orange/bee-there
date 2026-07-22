package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.Coffee;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class CoffeeRepository {
    private final Map<Long, Coffee> coffees = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Coffee> findAll() {
        return new ArrayList<>(coffees.values());
    }

    public Optional<Coffee> findById(Long id) {
        return Optional.ofNullable(coffees.get(id));
    }

    public Coffee save(Coffee coffee) {
        if (coffee.getId() == null) {
            coffee.setId(idGen.getAndIncrement());
        }
        coffees.put(coffee.getId(), coffee);
        return coffee;
    }

    public void deleteById(Long id) {
        coffees.remove(id);
    }
}
