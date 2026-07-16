package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    public List<Resource> getAll() {
        return repository.findAll();
    }

    public Optional<Resource> getById(Long id) {
        return repository.findById(id);
    }

    public Resource create(Resource resource) {
        return repository.save(resource);
    }

    public Resource update(Long id, Resource resource) {
        return repository.update(id, resource);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
