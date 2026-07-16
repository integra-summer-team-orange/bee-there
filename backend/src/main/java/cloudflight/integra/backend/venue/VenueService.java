package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing {@link Venue} entities.
 * Handles the core business logic and acts as an intermediary between the controller and the repository.
 */
@Service
public class VenueService {
    private final VenueRepository repository;

    public VenueService(VenueRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all available venues from the system.
     *
     * @return A list containing all {@link Venue} entities.
     */
    public List<Venue> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a specific venue by its unique identifier.
     *
     * @param id The unique identifier of the venue to retrieve.
     * @return An {@link Optional} containing the venue if it exists, or empty otherwise.
     */
    public Optional<Venue> getById(Long id) {
        return repository.findById(id);
    }

    /**
     * Persists a new venue in the system.
     *
     * @param venue The {@link Venue} entity containing the data to be saved.
     * @return The saved {@link Venue} entity with its generated ID and creation timestamp.
     */
    public Venue create(Venue venue) {
        return repository.save(venue);
    }

    /**
     * Updates an existing venue with new data.
     *
     * @param id The unique identifier of the venue to be updated.
     * @param venue The {@link Venue} entity containing the updated information.
     * @return The updated {@link Venue} entity.
     */
    public Venue update(Long id, Venue venue) {
        venue.setId(id);
        return repository.update(venue);
    }

    /**
     * Removes a venue from the system by its ID.
     *
     * @param id The unique identifier of the venue to be deleted.
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
