package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.exceptions.ErrorResponse;
import cloudflight.integra.backend.venue.model.VenueDto;
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
import java.util.List;
import org.springframework.http.HttpStatus;
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
     * Retrieves a list of all venues present in the system.
     *
     * @return A {@link ResponseEntity} containing a list of {@link VenueDto} with a 200 OK status.
     */
    @Operation(summary = "List all venues", description = "Returns every venue currently stored in the system.")
    @ApiResponse(
            responseCode = "200",
            description = "Venues retrieved successfully",
            content =
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = VenueDto.class))))
    @GetMapping
    public ResponseEntity<List<VenueDto>> getAll() {
        return ResponseEntity.ok(service.getAll().stream().map(mapper::toDto).toList());
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
    public ResponseEntity<VenueDto> getById(
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
                                        schema = @Schema(implementation = VenueDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping
    public ResponseEntity<VenueDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = @Content(examples = @ExampleObject(name = "New venue", value = """
                        {
                            "managedBy": 1,
                            "name": "Cloudflight Arena",
                            "description": "Indoor multi-purpose hall with 3 basketball courts",
                            "address": "Strada Republicii 42, Cluj-Napoca, Romania"
                        }
                        """)))
                    @RequestBody
                    @Valid
                    VenueDto dto) {
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
                                        schema = @Schema(implementation = VenueDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Venue not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<VenueDto> update(
            @Parameter(description = "ID of the venue to be updated", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = @Content(examples = @ExampleObject(name = "Updated venue", value = """
                        {
                            "managedBy": 1,
                            "name": "Cloudflight Arena - Renovated Wing",
                            "description": "Indoor multi-purpose hall, renovated east wing, 4 courts",
                            "address": "Strada Republicii 42, Cluj-Napoca, Romania"
                        }
                        """)))
                    @RequestBody
                    @Valid
                    VenueDto dto) {
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
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the venue to be deleted", required = true) @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
