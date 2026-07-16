package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.EntityNotFoundException;
import cloudflight.integra.backend.resources.model.Resource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ResourceRepository {
    private final ConcurrentHashMap<Long, Resource> resources = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all resources from the repository.
     *
     * @return a list of all resources
     */
    public List<Resource> findAll() {
        return new ArrayList<>(resources.values());
    }

    /**
     * Finds a resource by its identifier.
     *
     * @param id the unique identifier
     * @return an Optional containing the resource if found, or empty otherwise
     */
    public Optional<Resource> findById(Long id) {
        return Optional.ofNullable(resources.get(id));
    }


    /**
     * Saves a new resource to the repository.
     *
     * @param resource the resource to save
     * @return the saved resource with its generated identifier
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
     * @param id       the unique identifier of the resource
     * @param resource the updated resource data
     * @return the updated resource
     * @throws EntityNotFoundException if the resource does not exist
     */
    public Resource update(Long id, Resource resource) {
        if (!resources.containsKey(id)) {
            throw new EntityNotFoundException("Resource not found");
        }
        resource.setId(id);
        resources.put(id, resource);
        return resource;
    }

    /**
     * Deletes a resource from the repository.
     *
     * @param id the unique identifier of the resource to delete
     * @throws EntityNotFoundException if the resource does not exist
     */
    public void deleteById(Long id) {
        if(resources.remove(id) == null) {
            throw new EntityNotFoundException("Resource not found");
        }
    }
}
