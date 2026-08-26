package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.exceptions.ErrorResponse;
import cloudflight.integra.backend.venue.model.Venue;
import cloudflight.integra.backend.venue.model.VenueDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing venue-related API endpoints.
 * Handles incoming HTTP requests for creating, reading, updating, and deleting venues.
 */
@Tag(name = "Venues", description = "Endpoints for creating, retrieving, updating, and deleting venues.")
@RestController
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService service;
    private final VenueMapper mapper;

    /**
     * Creates a new venue controller.
     *
     * @param service the venue service
     * @param mapper the venue mapper
     */
    public VenueController(VenueService service, VenueMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves a paginated list of all venues present in the system.
     *
     * @param pageNumber The page index to retrieve (zero-based).
     * @param pageSize The number of venues per page.
     * @return A {@link ResponseEntity} containing a {@link Page} of {@link VenueDto} with a 200 OK status.
     */
    @Operation(
            summary = "List venues with pagination",
            description = "Returns a paginated list of venues stored in the system.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Paginated venues retrieved successfully"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid pagination parameters",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error occurred",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping
    public ResponseEntity<Page<VenueDto>> getAllVenues(
            @RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") int pageSize) {
        Page<Venue> venuePage = service.getAll(pageNumber, pageSize);
        Page<VenueDto> responsePage = venuePage.map(mapper::toDto);

        return ResponseEntity.ok(responsePage);
    }

    /**
     * Retrieves a paginated list of the venues managed by the currently authenticated user.
     *
     * @param pageNumber The page index to retrieve (zero-based).
     * @param pageSize The number of venues per page.
     * @param search Text matched against the venue name and address, or {@code null} for no filtering.
     * @return A {@link ResponseEntity} containing a {@link Page} of {@link VenueDto} with a 200 OK status.
     */
    @Operation(
            summary = "List the venues managed by the current user",
            description = "Returns a paginated list of the venues the authenticated caller manages. Callers who"
                    + " manage no venues receive an empty page rather than an error.")
    @ApiResponse(responseCode = "200", description = "Managed venues retrieved successfully")
    @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<VenueDto>> getMine(
            @RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") int pageSize,
            @Parameter(description = "Text matched against the venue name and address")
                    @RequestParam(required = false, name = "search")
                    String search) {
        Page<Venue> venuePage = service.getManagedByCurrentUser(pageNumber, pageSize, search);

        return ResponseEntity.ok(venuePage.map(mapper::toDto));
    }

    /**
     * Retrieves the details of a specific venue based on its ID.
     *
     * @param id The unique identifier of the requested venue.
     * @return A {@link ResponseEntity} containing the {@link VenueDto} with a 200 OK status.
     * @throws EntityNotFoundException if no venue is found matching the provided ID.
     */
    @Operation(summary = "Get a venue by ID", description = "Retrieves a single venue by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Venue found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = VenueDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Venue not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<VenueDto> getVenueById(
            @Parameter(description = "ID of the venue to be retrieved", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Venue with id: " + id + " not found!")));
    }

    /**
     * Creates a new venue based on the provided request payload.
     *
     * @param dto The validated {@link VenueDto} containing the venue details.
     * @return A {@link ResponseEntity} containing the created {@link VenueDto} with a 201 CREATED status.
     */
    @Operation(summary = "Create a new venue", description = "Adds a new venue to the system.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Venue successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = VenueDto.class),
                                        examples = @ExampleObject(name = "Created Venue Response", value = """
                                                {
                                                    "id": 1,
                                                    "managedBy": 1,
                                                    "managedByName": "Venue Admin",
                                                    "name": "Cloudflight Arena",
                                                    "description": "Indoor hall with 3 basketball courts",
                                                    "address": "Strada Republicii 42, Cluj-Napoca, Romania",
                                                    "createdAt": "2026-07-31T10:00:00"
                                                }"""))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping
    public ResponseEntity<VenueDto> createVenue(@RequestBody @Valid VenueDto dto) {
        VenueDto createdVenue = mapper.toDto(service.create(mapper.toEntity(dto)));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    /**
     * Updates an existing venue entirely with the provided payload.
     *
     * @param id The unique identifier of the venue to be updated.
     * @param dto The validated {@link VenueDto} containing the new data.
     * @return A {@link ResponseEntity} containing the updated {@link VenueDto} with a 200 OK status.
     */
    @Operation(summary = "Update an existing venue", description = "Updates the venue details for the given ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Venue successfully updated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = VenueDto.class),
                                        examples = @ExampleObject(name = "Updated Venue Response", value = """
                                                {
                                                    "id": 1,
                                                    "managedBy": 1,
                                                    "managedByName": "Venue Admin",
                                                    "name": "Cloudflight Arena - Renovated Wing",
                                                    "description": "Indoor hall, renovated east wing, 4 courts",
                                                    "address": "Strada Republicii 42, Cluj-Napoca, Romania",
                                                    "createdAt": "2026-07-31T10:00:00"
                                                }"""))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Venue not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "The caller does not manage this venue",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<VenueDto> updateVenue(
            @Parameter(description = "ID of the venue to be updated", required = true) @PathVariable Long id,
            @RequestBody @Valid VenueDto dto) {
        return ResponseEntity.ok(mapper.toDto(service.update(id, mapper.toEntity(dto))));
    }

    /**
     * Deletes a specific venue from the system by its ID.
     *
     * @param id The unique identifier of the venue to be removed.
     * @return An empty {@link ResponseEntity} with a 204 NO CONTENT status.
     */
    @Operation(summary = "Delete a venue", description = "Removes a venue by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Venue successfully deleted", content = @Content),
                @ApiResponse(
                        responseCode = "404",
                        description = "Venue not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "403",
                        description = "The caller does not manage this venue",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(
            @Parameter(description = "ID of the venue to be deleted", required = true) @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
