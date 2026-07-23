package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Resource", description = "Endpoints for managing resources")
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
    @Operation(summary = "Get all resources", description = "Retrieves a list of all available resources.")
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of resources",
            content =
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ResourceDto.class))))
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
    @Operation(summary = "Get a resource by ID", description = "Retrieves a single resource by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the resource",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResourceDto.class))),
                @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
            })
    public ResponseEntity<ResourceDto> getById(
            @Parameter(description = "ID of the resource to be retrieved", required = true) @PathVariable Long id) {
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
    @Operation(summary = "Create a new resource", description = "Adds a new resource to the system.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Resource successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResourceDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            })
    public ResponseEntity<ResourceDto> create(
            @Parameter(description = "Resource object to create", required = true) @Valid @RequestBody
                    ResourceDto dto) {
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
    @Operation(summary = "Update an existing resource", description = "Updates the resource details for the given ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Resource successfully updated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResourceDto.class))),
                @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content),
                @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
            })
    public ResponseEntity<ResourceDto> update(
            @Parameter(description = "ID of the resource to be updated", required = true) @PathVariable Long id,
            @Parameter(description = "Updated resource object data", required = true) @Valid @RequestBody
                    ResourceDto dto) {
        return ResponseEntity.ok(mapper.toDto(service.update(id, mapper.toEntity(dto))));
    }

    /**
     * Deletes a specific resource.
     *
     * @param id The unique identifier of the resource to delete.
     * @return An empty {@link ResponseEntity} with a 204 NO CONTENT status.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resource", description = "Removes a resource by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Resource successfully deleted", content = @Content),
                @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content)
            })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the resource to be deleted", required = true) @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
