package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.resources.model.Resource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing {@link Resource} business logic.
 * Acts as an intermediary between the controller and the repository.
 */
@Service
public class ResourceService {
    private final ResourceRepository repository;

    /**
     * Creates a new resource service.
     *
     * @param repository the resource repository
     */
    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all resources.
     *
     * @return A list of all {@link Resource} entities.
     */
    public List<Resource> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a paginated list of resources.
     *
     * @param page the page index to retrieve (zero-based)
     * @param size the number of resources to include on each page
     * @return a {@code Page} containing the resources for the requested page
     */
    public Page<Resource> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

    /**
     * Retrieves a resource by its unique identifier.
     *
     * @param id The unique identifier.
     * @return An {@link Optional} containing the resource if found.
     */
    public Optional<Resource> getById(Long id) {
        return repository.findById(id);
    }

    /**
     * Creates a new resource.
     *
     * @param resource The resource entity to create.
     * @return The created {@link Resource} entity.
     */
    public Resource create(Resource resource) {
        return repository.save(resource);
    }

    /**
     * Updates an existing resource.
     *
     * @param id       The unique identifier of the resource.
     * @param resource The updated resource data.
     * @return The updated {@link Resource} entity.
     * @throws EntityNotFoundException if the resource does not exist.
     */
    public Resource update(Long id, Resource resource) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Resource not found with id: " + id);
        }
        resource.setId(id);
        return repository.save(resource);
    }

    /**
     * Deletes a resource by its identifier.
     *
     * @param id The unique identifier of the resource.
     * @throws EntityNotFoundException if the resource does not exist.
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Resource not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
