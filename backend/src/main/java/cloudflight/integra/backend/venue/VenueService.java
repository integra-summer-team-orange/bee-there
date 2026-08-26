package cloudflight.integra.backend.venue;

import static cloudflight.integra.backend.authentication.config.SecurityUtils.checkOwnership;
import static cloudflight.integra.backend.authentication.config.SecurityUtils.getCurrentUser;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.venue.model.Venue;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing {@link Venue} entities.
 * Handles the core business logic and acts as an intermediary between the controller and the repository.
 */
@Service
public class VenueService {
    private final VenueRepository repository;

    /**
     * Creates a new venue service.
     *
     * @param repository the venue repository
     */
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
     * Retrieves a paginated list of venues.
     *
     * @param page the page index to retrieve (zero-based)
     * @param size the number of venues to include on each page
     * @return a {@code Page} containing the venues for the requested page
     */
    public Page<Venue> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    /**
     * Retrieves a paginated list of the venues managed by the currently authenticated user, optionally
     * narrowed to those whose name or address contains the search text.
     *
     * @param page the page index to retrieve (zero-based)
     * @param size the number of venues to include on each page
     * @param search the text to match against name and address, or {@code null} for no filtering
     * @return a {@code Page} containing only the venues the caller manages
     */
    public Page<Venue> getManagedByCurrentUser(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Long managedById = getCurrentUser().getId();

        if (search == null || search.isBlank()) {
            return repository.findAllByManagedById(managedById, pageable);
        }

        return repository.findAllByManagedByIdMatching(managedById, search.trim(), pageable);
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
     * Persists a new venue in the system, owned by the currently authenticated user.
     * The managing user is taken from the security context rather than the request, so a caller cannot
     * create a venue on someone else's behalf.
     *
     * @param venue The {@link Venue} entity containing the data to be saved.
     * @return The saved {@link Venue} entity with its generated ID and creation timestamp.
     */
    public Venue create(Venue venue) {
        venue.setManagedBy(getCurrentUser());
        return repository.save(venue);
    }

    /**
     * Updates an existing venue with new data.
     * The managing user and creation timestamp are carried over from the stored row, so an update can never
     * transfer a venue to a different owner.
     *
     * @param id The unique identifier of the venue to be updated.
     * @param venue The {@link Venue} entity containing the updated information.
     * @return The updated {@link Venue} entity.
     * @throws EntityNotFoundException if the venue does not exist.
     */
    public Venue update(Long id, Venue venue) {
        Optional<Venue> existing = repository.findById(id);
        if (existing.isEmpty()) {
            throw new EntityNotFoundException("Venue with id: " + id + " not found!");
        }
        checkOwnership(existing.get().getManagedBy().getId());

        venue.setId(id);
        venue.setCreatedAt(existing.get().getCreatedAt());
        venue.setManagedBy(existing.get().getManagedBy());

        repository.save(venue);

        // Saving an entity that already has an id merges it, and the merge hands the managing user back as a
        // lazy proxy. Re-read through the entity graph so the caller still sees an initialised owner.
        return repository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venue with id: " + id + " not found!"));
    }

    /**
     * Removes a venue from the system by its ID.
     *
     * @param id The unique identifier of the venue to be deleted.
     * @throws EntityNotFoundException if the venue does not exist.
     */
    public void delete(Long id) {
        Optional<Venue> venue = repository.findById(id);
        if (venue.isEmpty()) {
            throw new EntityNotFoundException("Venue with id: " + id + " not found!");
        }
        checkOwnership(venue.get().getManagedBy().getId());
        repository.deleteById(id);
    }
}
