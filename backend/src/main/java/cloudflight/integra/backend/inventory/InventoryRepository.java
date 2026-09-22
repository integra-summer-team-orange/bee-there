package cloudflight.integra.backend.inventory;

import cloudflight.integra.backend.inventory.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Inventory} entities.
 * Provides standard CRUD (Create, Read, Update, Delete) operations and pagination capabilities
 * backed by the database.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    /**
     * Retrieves a paginated list of inventory items associated with a given Venue.
     *
     * @param venueId The unique identifier of the venue.
     * @param pageable Pagination details
     * @return A Page of Inventory items
     */
    Page<Inventory> findByVenueId(Long venueId, Pageable pageable);
}
