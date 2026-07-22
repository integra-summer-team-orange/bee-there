package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.resources.model.ResourceDto;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * REST Controller for managing resource API endpoints.
 */
@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private final ResourceService service;
    private final ResourceMapper mapper;

    public ResourceController(ResourceService service, ResourceMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves all resources.
     *
     * @return A {@link ResponseEntity} containing a list of {@link ResourceDto} with a 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<ResourceDto>> getAll() {
        return ResponseEntity.ok(service.getAll().stream().map(mapper::toDto).toList());
    }

    /**
     * Retrieves a specific resource by its identifier.
     *
     * @param id The unique identifier of the resource.
     * @return A {@link ResponseEntity} containing the {@link ResourceDto} with a 200 OK status.
     * @throws EntityNotFoundException if the resource is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found with id: " + id));
    }

    /**
     * Creates a new resource.
     *
     * @param dto The validated {@link ResourceDto} payload.
     * @return A {@link ResponseEntity} containing the created {@link ResourceDto} with a 201 CREATED status
     * and a Location header.
     */
    @PostMapping
    public ResponseEntity<ResourceDto> create(@Valid @RequestBody ResourceDto dto) {
        ResourceDto created = mapper.toDto(service.create(mapper.toEntity(dto)));
        URI location = UriComponentsBuilder.fromPath("/api/resources/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Updates an existing resource.
     *
     * @param id  The unique identifier of the resource to update.
     * @param dto The validated {@link ResourceDto} payload.
     * @return A {@link ResponseEntity} containing the updated {@link ResourceDto} with a 200 OK status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResourceDto> update(@PathVariable Long id, @Valid @RequestBody ResourceDto dto) {
        return ResponseEntity.ok(mapper.toDto(service.update(id, mapper.toEntity(dto))));
    }

    /**
     * Deletes a specific resource.
     *
     * @param id The unique identifier of the resource to delete.
     * @return An empty {@link ResponseEntity} with a 204 NO CONTENT status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
