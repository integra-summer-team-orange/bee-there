package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.EntityNotFoundException;
import cloudflight.integra.backend.resources.model.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resources", description = "Endpoints for managing bookable resources")
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
    @Operation(summary = "List all resources", description = "Returns all resources from the in-memory store")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
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
    @Operation(summary = "Get resource by ID", description = "Returns a single resource")
    @ApiResponse(responseCode = "200", description = "Resource found")
    @ApiResponse(responseCode = "404", description = "Resource not found")
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
    @Operation(summary = "Create a resource", description = "Creates a new bookable resource")
    @ApiResponse(responseCode = "201", description = "Resource created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    public ResponseEntity<ResourceResponse> create(@Valid @RequestBody ResourceRequest dto) {
        ResourceResponse created = mapper.toDto(service.create(mapper.toEntity(dto)));
        return ResponseEntity.created(URI.create("/api/resources/" + created.getId())).body(created);
    }

    /**
     * Creates a new resource.
     *
     * @param dto the resource request payload
     * @return a response entity containing the created resource
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a resource", description = "Updates an existing resource entirely")
    @ApiResponse(responseCode = "200", description = "Resource updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Resource not found")
    public ResourceResponse update(@PathVariable Long id, @Valid @RequestBody ResourceRequest dto) {
        Resource updated = service.update(id, mapper.toEntity(dto));
        return mapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resource", description = "Removes a resource from the system")
    @ApiResponse(responseCode = "204", description = "Resource deleted successfully")
    @ApiResponse(responseCode = "404", description = "Resource not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
