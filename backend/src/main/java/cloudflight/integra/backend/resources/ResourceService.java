package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    private final ResourceRepository repository;

    /**
     * Constructs a new ResourceService.
     *
     * @param repository the resource repository
     */
    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all resources.
     *
     * @return a list of all resources
     */
    public List<Resource> getAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a resource by its identifier.
     *
     * @param id the unique identifier
     * @return an Optional containing the resource if found
     */
    public Optional<Resource> getById(Long id) {
        return repository.findById(id);
    }

    /**
     * Creates a new resource.
     *
     * @param resource the resource entity to create
     * @return the created resource
     */
    public Resource create(Resource resource) {
        return repository.save(resource);
    }

    /**
     * Updates an existing resource.
     *
     * @param id       the unique identifier of the resource
     * @param resource the updated resource data
     * @return the updated resource
     */
    public Resource update(Long id, Resource resource) {
        return repository.update(id, resource);
    }

    /**
     * Deletes a resource.
     *
     * @param id the unique identifier of the resource to delete
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
