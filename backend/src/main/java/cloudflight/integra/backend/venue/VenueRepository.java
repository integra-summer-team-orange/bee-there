package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing venue entities.
 */
@Repository
public class VenueRepository {
    private final Map<Long, Venue> venues = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all venues.
     *
     * @return a list of all venues
     */
    public List<Venue> findAll() {
        return new ArrayList<>(venues.values());
    }

    /**
     * Retrieves a venue by its unique identifier.
     *
     * @param id the identifier of the venue to retrieve
     * @return an optional containing the requested venue if found, otherwise an empty optional
     */
    public Optional<Venue> findById(Long id) {
        return Optional.ofNullable(venues.get(id));
    }

    /**
     * Saves a venue.
     *
     * <p>If the venue does not yet have an identifier, a new one is generated and the
     * creation timestamp is set.</p>
     *
     * @param venue the venue to save
     * @return the saved venue
     */
    public Venue save(Venue venue) {
        if (venue.getId() == null) {
            venue.setId(idGen.getAndIncrement());
            venue.setCreatedAt(LocalDateTime.now());
        }
        venues.put(venue.getId(), venue);
        return venue;
    }

    /**
     * Updates an existing venue.
     *
     * @param venue the venue to update
     * @return the updated venue
     */
    public Venue update(Venue venue) {
        venues.put(venue.getId(), venue);
        return venue;
    }

    /**
     * Deletes a venue by its unique identifier.
     *
     * @param id the identifier of the venue to delete
     */
    public void deleteById(Long id) {
        venues.remove(id);
    }
}
