package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory repository for managing {@link Resource} entities.
 * Utilizes a thread-safe map for storage and an atomic counter for ID generation.
 */
@Repository
public class ResourceRepository {
    private final ConcurrentHashMap<Long, Resource> resources = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all resources from the repository.
     *
     * @return A list of all {@link Resource} entities.
     */
    public List<Resource> findAll() {
        return new ArrayList<>(resources.values());
    }

    /**
     * Retrieves a resource by its unique identifier.
     *
     * @param id The unique identifier of the resource.
     * @return An {@link Optional} containing the resource if found, or empty otherwise.
     */
    public Optional<Resource> findById(Long id) {
        return Optional.ofNullable(resources.get(id));
    }

    /**
     * Saves a new resource to the repository and assigns it a generated identifier.
     *
     * @param resource The resource to save.
     * @return The saved {@link Resource} entity.
     */
    public Resource save(Resource resource) {
        if (resource.getId() == null) {
            resource.setId(idGen.getAndIncrement());
        }
        resources.put(resource.getId(), resource);
        return resource;
    }

    /**
     * Updates an existing resource in the repository.
     *
     * @param id       The unique identifier of the resource to update.
     * @param resource The updated resource data.
     * @return An {@link Optional} containing the updated resource, or empty if the resource does not exist.
     */
    public Optional<Resource> update(Long id, Resource resource) {
        if (!resources.containsKey(id)) {
            return Optional.empty();
        }
        resource.setId(id);
        resources.put(id, resource);
        return Optional.of(resource);
    }

    /**
     * Deletes a resource from the repository by its identifier.
     *
     * @param id The unique identifier of the resource to delete.
     * @return true if the resource was deleted, false if it did not exist.
     */
    public boolean deleteById(Long id) {
        return resources.remove(id) != null;
    }
}
