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

    public List<Resource> findAll() {
        return new ArrayList<>(resources.values());
    }

    public Optional<Resource> findById(Long id) {
        return Optional.ofNullable(resources.get(id));
    }

    public Resource save(Resource resource) {
        if (resource.getId() == null) {
            resource.setId(idGen.getAndIncrement());
        }
        resources.put(resource.getId(), resource);
        return resource;
    }

    public Resource update(Long id, Resource resource) {
        if (!resources.containsKey(id)) {
            throw new EntityNotFoundException("Resource not found");
        }
        resource.setId(id);
        resources.put(id, resource);
        return resource;
    }

    public void deleteById(Long id) {
        if(resources.remove(id) == null) {
            throw new EntityNotFoundException("Resource not found");
        }
    }
}
