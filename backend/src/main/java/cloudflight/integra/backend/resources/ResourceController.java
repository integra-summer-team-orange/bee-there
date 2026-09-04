package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.exceptions.ErrorResponse;
import cloudflight.integra.backend.resources.model.ResourceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
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
        description = "API endpoints for managing various types of resources "
                + "(sports courts, social spaces, etc.) within venues.")
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
     * Retrieves a paginated list of resources.
     *
     * @param pageNumber the zero-based page index to retrieve
     * @param pageSize the maximum number of resources to return per page
     * @return A {@link ResponseEntity} containing the requested page of {@link ResourceDto} with a 200 OK status.
     */
    @Operation(
            summary = "List resources with pagination",
            description = "Returns a paginated list of resources stored in the system.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Paginated resources retrieved successfully"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid pagination parameters",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error occurred",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ResourceDto>> getAllResources(
            @RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") int pageSize) {
        return ResponseEntity.ok(service.getAll(pageNumber, pageSize).map(mapper::toDto));
    }

    /**
     * Retrieves a specific resource by its identifier.
     *
     * @param id The unique identifier of the resource.
     * @return A {@link ResponseEntity} containing the {@link ResourceDto} with a 200 OK status.
     * @throws EntityNotFoundException if the resource is not found.
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
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
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error occurred",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<ResourceDto> getResourceById(
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
    @Operation(
            summary = "Create a new resource",
            description = "Registers a new resource in the system. "
                    + "Allowed values for the 'type' field: INDOOR_SPORT, OUTDOOR_SPORT, BOARDGAME_SOCIAL")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Resource successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ResourceDto.class),
                                        examples = @ExampleObject(name = "Resource Example", value = """
                                                            {
                                                                "venueId": 1,
                                                                "name": "Badminton Court 1",
                                                                "activityType": "Badminton",
                                                                "activityDescription": "A full-sized badminton court.",
                                                                "type": "INDOOR_SPORT",
                                                                "capacity": 4,
                                                                "hourlyRate": 15.00
                                                            }
                                                            """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid resource data provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error occurred",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<ResourceDto> createResource(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            description = "The resource data to create. "
                                    + "Must include venueId, name, activityType, type, capacity, and hourlyRate."
                                    + " Optional fields: activityDescription.",
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ResourceDto.class),
                                            examples = @ExampleObject(name = "Valid Resource Request", value = """
                                                            {
                                                                "venueId": 1,
                                                                "name": "Badminton Court 1",
                                                                "activityType": "Badminton",
                                                                "activityDescription": "A full-sized badminton court.",
                                                                "type": "INDOOR_SPORT",
                                                                "capacity": 4,
                                                                "hourlyRate": 15.00
                                                            }
                                                            """)))
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
                                        schema = @Schema(implementation = ResourceDto.class),
                                        examples = @ExampleObject(name = "Resource Example", value = """
                                                {
                                                "venueId": 1,
                                                "name": "Badminton Court 1",
                                                "activityType": "Badminton",
                                                "activityDescription": "A full-sized badminton court.",
                                                "type": "INDOOR_SPORT",
                                                "capacity": 4,
                                                "hourlyRate": 15.00
                                                }
                                                """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid resource data provided",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Resource with the given ID not found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error occurred",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<ResourceDto> updateResource(
            @Parameter(description = "The unique identifier of the resource to update", example = "1") @PathVariable
                    Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            description = "The resource data to update."
                                    + " Must include venueId, name, activityType, type, capacity, and hourlyRate. "
                                    + "Optional fields: activityDescription.",
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ResourceDto.class),
                                            examples =
                                                    @ExampleObject(
                                                            name = "Valid Resource Update Request",
                                                            value = """
                                                            {
                                                                "venueId": 1,
                                                                "name": "Badminton Court 1",
                                                                "activityType": "Badminton",
                                                                "activityDescription": "A full-sized badminton court.",
                                                                "type": "INDOOR_SPORT",
                                                                "capacity": 4,
                                                                "hourlyRate": 15.00
                                                            }
                                                            """)))
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
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error occurred",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<Void> deleteResource(
            @Parameter(description = "The unique identifier of the resource to delete", example = "1") @PathVariable
                    Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
