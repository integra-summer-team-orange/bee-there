package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing venue entities.
 */
@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {}
