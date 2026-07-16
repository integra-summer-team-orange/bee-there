package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.EntityNotFoundException;
import cloudflight.integra.backend.resources.model.Resource;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private final ResourceService service;
    private final ResourceMapper mapper;

    /**
     * Constructs a new ResourceController.
     *
     * @param service the resource service
     * @param mapper  the resource mapper
     */
    public ResourceController(ResourceService service, ResourceMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves all resources.
     *
     * @return a list of resource responses
     */
    @GetMapping
    public List<ResourceResponse> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    /**
     * Retrieves a resource by its identifier.
     *
     * @param id the unique identifier of the resource
     * @return the resource response
     * @throws EntityNotFoundException if the resource is not found
     */
    @GetMapping("/{id}")
    public ResourceResponse getById(@PathVariable Long id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }

    /**
     * Creates a new resource.
     *
     * @param dto the resource request payload
     * @return a response entity containing the created resource
     */
    @PostMapping
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceRequest dto) {
        ResourceResponse created = mapper.toDto(service.create(mapper.toEntity(dto)));
        return ResponseEntity.created(URI.create("/api/resources/" + created.getId())).body(created);
    }

    /**
     * Updates an existing resource.
     *
     * @param id  the unique identifier of the resource to update
     * @param dto the resource request payload
     * @return the updated resource response
     */
    @PutMapping("/{id}")
    public ResourceResponse update(@PathVariable Long id, @Valid @RequestBody ResourceRequest dto) {
        Resource updated = service.update(id, mapper.toEntity(dto));
        return mapper.toDto(updated);
    }

    /**
     * Deletes a resource by its identifier.
     *
     * @param id the unique identifier of the resource to delete
     * @return an empty response entity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
