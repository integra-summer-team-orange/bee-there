package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.ResourceDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing venue-specific resource endpoints.
 * Separated from ResourceController to cleanly handle sub-resource routing.
 */
@RestController
@RequestMapping("/api/venues")
public class VenueResourceController {

    private final ResourceService service;
    private final ResourceMapper mapper;

    /**
     * Creates a new VenueResourceController
     *
     * @param service the resource service
     * @param mapper the resource mapper
     */
    public VenueResourceController(ResourceService service, ResourceMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Retrieves a paginated list of resources associated with a specific venue.
     *
     * @param id         The unique identifier of the venue.
     * @param pageNumber The page index for pagination (zero-based, default is 0).
     * @param pageSize   The number of items per page (default is 10).
     * @return A {@link ResponseEntity} containing a paginated list of {@link ResourceDto}.
     */
    @GetMapping("/{id}/resources")
    @Tag(name = "Venues")
    public ResponseEntity<Page<ResourceDto>> getResourcesByVenue(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0", name = "pageNumber") int pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") int pageSize) {

        return ResponseEntity.ok(service.getByVenueId(id, pageNumber, pageSize).map(mapper::toDto));
    }
}
