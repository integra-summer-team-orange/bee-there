package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing resource entities.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    /**
     * Retrieves a paginated list of resources belonging to a specific venue.
     *
     * @param venueId  The unique identifier of the venue.
     * @param pageable Pagination details.
     * @return A page of resources.
     */
    Page<Resource> findByVenueId(Long venueId, Pageable pageable);
}
