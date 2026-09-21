package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing venue entities.
 * Every read fetches the managing user alongside the venue: {@code open-in-view} is disabled, so the mapper
 * runs outside the persistence session and a lazy proxy would fail once its name is read.
 */
@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

    /**
     * Retrieves all venues together with their managing user.
     *
     * @return every stored venue
     */
    @Override
    @EntityGraph(attributePaths = "managedBy")
    List<Venue> findAll();

    /**
     * Retrieves a page of venues together with their managing user.
     *
     * @param pageable the requested page and size
     * @return the requested page of venues
     */
    @Override
    @EntityGraph(attributePaths = "managedBy")
    Page<Venue> findAll(Pageable pageable);

    /**
     * Retrieves a single venue together with its managing user.
     *
     * @param id the id of the venue to look up
     * @return the venue if it exists, empty otherwise
     */
    @Override
    @EntityGraph(attributePaths = "managedBy")
    Optional<Venue> findById(Long id);

    /**
     * Retrieves a page of the venues managed by a given user.
     *
     * @param managedById the id of the managing user
     * @param pageable the requested page and size
     * @return a page containing only the venues managed by that user
     */
    @EntityGraph(attributePaths = "managedBy")
    Page<Venue> findAllByManagedById(Long managedById, Pageable pageable);

    /**
     * Retrieves a page of the venues managed by a given user whose name or address contains the search text.
     *
     * @param managedById the id of the managing user
     * @param search the text to look for, matched case-insensitively
     * @param pageable the requested page and size
     * @return a page containing only the matching venues
     */
    @EntityGraph(attributePaths = "managedBy")
    @Query("""
            select v from Venue v
            where v.managedBy.id = :managedById
              and (lower(v.name) like lower(concat('%', :search, '%'))
                or lower(v.address) like lower(concat('%', :search, '%')))
            """)
    Page<Venue> findAllByManagedByIdMatching(
            @Param("managedById") Long managedById, @Param("search") String search, Pageable pageable);
}
