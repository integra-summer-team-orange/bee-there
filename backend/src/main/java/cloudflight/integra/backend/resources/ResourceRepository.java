package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import cloudflight.integra.backend.resources.model.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Retrieves a paginated list of a venue's resources whose name or activity type contains the search text,
     * optionally narrowed to one resource type.
     *
     * @param venueId  The unique identifier of the venue.
     * @param search   The text to look for, matched case-insensitively; an empty string matches everything.
     * @param type     The resource type to keep, or {@code null} for all types.
     * @param pageable Pagination details.
     * @return A page of the matching resources.
     */
    @Query("""
            select r from Resource r
            where r.venue.id = :venueId
              and (lower(r.name) like lower(concat('%', :search, '%'))
                or lower(r.activityType) like lower(concat('%', :search, '%')))
              and (:type is null or r.type = :type)
            """)
    Page<Resource> findByVenueIdMatching(
            @Param("venueId") Long venueId,
            @Param("search") String search,
            @Param("type") ResourceType type,
            Pageable pageable);
}
