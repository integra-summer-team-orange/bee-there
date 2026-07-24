package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.resources.model.ResourceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@Tag(
        name = "Resources",
        description =
                "Endpoints for managing various types of resources (sports courts, social spaces, etc.) within venues.")
public class ResourceController {
    private final ResourceService service;
    private final ResourceMapper mapper;

    /**
     * Creates a new resource controller.
     *
     * @param service the resource service
     * @param mapper the resource mapper
     */
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
    @Operation(summary = "Get all resources", description = "Returns a list of all resources available in the system.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "List of resources successfully retrieved",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        array = @ArraySchema(schema = @Schema(implementation = ResourceDto.class)))),
                @ApiResponse(responseCode = "500", description = "Internal server error occurred", content = @Content)
            })
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
    @Operation(summary = "Get a resource by ID", description = "Returns a single resource based on its unique ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Resource found and returned",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResourceDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Resource with the given ID not found",
                        content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error occurred", content = @Content)
            })
    public ResponseEntity<ResourceDto> getById(
            @Parameter(description = "The unique identifier of the resource", example = "1") @PathVariable Long id) {
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
    @Operation(summary = "Create a new resource", description = "Registers a new resource in the system.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Resource successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResourceDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid resource data provided", content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error occurred", content = @Content)
            })
    public ResponseEntity<ResourceDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Details of the resource to be created",
                            content =
                                    @Content(
                                            examples =
                                                    @ExampleObject(
                                                            value =
                                                                    "{\"venueId\": 1, \"name\": \"Badminton Court 1\", \"activityType\": \"Badminton\", \"activityDescription\": \"Full-sized court\", \"type\": \"INDOOR_SPORT\", \"capacity\": 4, \"hourlyRate\": 15.00}")))
                    @Valid
                    @RequestBody
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
    @Operation(
            summary = "Update an existing resource",
            description = "Updates the details of a resource identified by ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Resource successfully updated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResourceDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid resource data provided", content = @Content),
                @ApiResponse(
                        responseCode = "404",
                        description = "Resource with the given ID not found",
                        content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error occurred", content = @Content)
            })
    public ResponseEntity<ResourceDto> update(
            @Parameter(description = "The unique identifier of the resource to update", example = "1") @PathVariable
                    Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "Updated resource data",
                            content =
                                    @Content(
                                            examples =
                                                    @ExampleObject(
                                                            value =
                                                                    "{\"venueId\": 1, \"name\": \"Updated Court Name\", \"activityType\": \"Badminton\", \"activityDescription\": \"Updated description\", \"type\": \"INDOOR_SPORT\", \"capacity\": 4, \"hourlyRate\": 18.00}")))
                    @Valid
                    @RequestBody
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
    @Operation(summary = "Delete a resource", description = "Permanently removes a resource from the system.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Resource successfully deleted", content = @Content),
                @ApiResponse(
                        responseCode = "404",
                        description = "Resource with the given ID not found",
                        content = @Content),
                @ApiResponse(responseCode = "500", description = "Internal server error occurred", content = @Content)
            })
    public ResponseEntity<Void> delete(
            @Parameter(description = "The unique identifier of the resource to delete", example = "1") @PathVariable
                    Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
