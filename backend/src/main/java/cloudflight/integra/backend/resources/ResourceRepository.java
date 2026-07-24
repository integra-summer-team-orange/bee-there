package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

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

    public Optional<Resource> update(Long id, Resource resource) {
        if (!resources.containsKey(id)) {
            return Optional.empty();
        }
        resource.setId(id);
        resources.put(id, resource);
        return Optional.of(resource);
    }

    public boolean deleteById(Long id) {
        return resources.remove(id) != null;
    }
}
