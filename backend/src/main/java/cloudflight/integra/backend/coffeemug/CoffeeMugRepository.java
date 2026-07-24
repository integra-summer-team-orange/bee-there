package cloudflight.integra.backend.coffeemug;

import cloudflight.integra.backend.coffeemug.model.CoffeeMug;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing coffee mug entities.
 */
@Repository
public class CoffeeMugRepository {
    private final Map<Long, CoffeeMug> mugs = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all coffee mugs.
     *
     * @return a list of all coffee mugs
     */
    public List<CoffeeMug> findAll() {
        return new ArrayList<>(mugs.values());
    }

    /**
     * Retrieves a coffee mug by its unique identifier.
     *
     * @param id the identifier of the coffee mug to retrieve
     * @return an optional containing the requested coffee mug if found, otherwise an empty optional
     */
    public Optional<CoffeeMug> findById(Long id) {
        return Optional.ofNullable(mugs.get(id));
    }

    /**
     * Saves a coffee mug.
     *
     * <p>If the coffee mug does not yet have an identifier, a new one is generated.</p>
     *
     * @param mug the coffee mug to save
     * @return the saved coffee mug
     */
    public CoffeeMug save(CoffeeMug mug) {
        if (mug.getId() == null) {
            mug.setId(idGen.getAndIncrement());
        }
        mugs.put(mug.getId(), mug);
        return mug;
    }

    /**
     * Deletes a coffee mug by its unique identifier.
     *
     * @param id the identifier of the coffee mug to delete
     */
    public void deleteById(Long id) {
        mugs.remove(id);
    }
}
