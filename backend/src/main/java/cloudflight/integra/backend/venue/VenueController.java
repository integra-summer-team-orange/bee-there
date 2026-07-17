package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.venue.model.VenueDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing venue-related API endpoints.
 * Handles incoming HTTP requests for creating, reading, updating, and deleting venues.
 */
@RestController
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService service;
    private final VenueMapper mapper;

    public VenueController(VenueService service, VenueMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves a list of all venues present in the system.
     *
     * @return A {@link ResponseEntity} containing a list of {@link VenueDto} with a 200 OK status.
     */
    @GetMapping
    public ResponseEntity<List<VenueDto>> getAll() {
        return ResponseEntity.ok(
            service.getAll().stream().map(mapper::toDto).toList()
        );
    }

    /**
     * Retrieves the details of a specific venue based on its ID.
     *
     * @param id The unique identifier of the requested venue.
     * @return A {@link ResponseEntity} containing the {@link VenueDto} with a 200 OK status.
     * @throws EntityNotFoundException if no venue is found matching the provided ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VenueDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            service.getById(id).map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Venue with id: " + id + " not found!"))
        );
    }

    /**
     * Creates a new venue based on the provided request payload.
     *
     * @param dto The validated {@link VenueDto} containing the venue details.
     * @return A {@link ResponseEntity} containing the created {@link VenueDto} with a 201 CREATED status.
     */
    @PostMapping
    public ResponseEntity<VenueDto> create(@RequestBody @Valid VenueDto dto) {
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
    @PutMapping("/{id}")
    public ResponseEntity<VenueDto> update(@PathVariable Long id, @RequestBody @Valid VenueDto dto) {
        return ResponseEntity.ok(
            mapper.toDto(service.update(id, mapper.toEntity(dto)))
        );
    }

    /**
     * Deletes a specific venue from the system by its ID.
     *
     * @param id The unique identifier of the venue to be removed.
     * @return An empty {@link ResponseEntity} with a 204 NO CONTENT status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
