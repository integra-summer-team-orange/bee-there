package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing resource entities.
 */
@Repository
public class ResourceRepository {
    private final ConcurrentHashMap<Long, Resource> resources = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all resources.
     *
     * @return a list of all resources
     */
    public List<Resource> findAll() {
        return new ArrayList<>(resources.values());
    }

    /**
     * Retrieves a resource by its unique identifier.
     *
     * @param id the identifier of the resource to retrieve
     * @return an optional containing the requested resource if found, otherwise an empty optional
     */
    public Optional<Resource> findById(Long id) {
        return Optional.ofNullable(resources.get(id));
    }

    /**
     * Saves a resource.
     *
     * <p>If the resource does not yet have an identifier, a new one is generated.</p>
     *
     * @param resource the resource to save
     * @return the saved resource
     */
    public Resource save(Resource resource) {
        if (resource.getId() == null) {
            resource.setId(idGen.getAndIncrement());
        }
        resources.put(resource.getId(), resource);
        return resource;
    }

    /**
     * Updates an existing resource.
     *
     * @param id the identifier of the resource to update
     * @param resource the updated resource
     * @return an optional containing the updated resource if it exists, otherwise an empty optional
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
     * Deletes a resource by its unique identifier.
     *
     * @param id the identifier of the resource to delete
     * @return {@code true} if the resource was deleted, otherwise {@code false}
     */
    public boolean deleteById(Long id) {
        return resources.remove(id) != null;
    }
}
