package cloudflight.integra.backend.reservation;

import cloudflight.integra.backend.reservation.model.Reservation;
import cloudflight.integra.backend.reservation.model.Status;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for accessing and managing {@link Reservation} entities.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Retrieves a paginated list of reservations belonging to the specified organizer.
     *
     * @param organizerId the identifier of the organizer
     * @param pageable the pagination information
     * @return a page containing the organizer's reservations
     */
    Page<Reservation> findAllByOrganizerId(Long organizerId, Pageable pageable);

    /**
     * Checks whether an active reservation exists for the specified resource
     * that overlaps with the given time interval.
     *
     * @param resourceId the identifier of the resource
     * @param status the reservation status to exclude from the overlap check
     * @param startTime the start of the requested reservation interval
     * @param endTime the end of the requested reservation interval
     * @return {@code true} if an overlapping reservation exists; {@code false} otherwise
     */
    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Reservation r
    WHERE r.resource.id = :resourceId
      AND r.status <> :status
      AND r.startTime < :endTime
      AND r.endTime > :startTime
""")
    boolean existsOverlappingReservation(
            @Param("resourceId") Long resourceId,
            @Param("status") Status status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Retrieves a paginated list of reservations belonging to the specified organizer
     * and associated with the specified venue.
     *
     * @param userId the identifier of the organizer
     * @param venueId the identifier of the venue
     * @param pageable the pagination information
     * @return a page containing the organizer's reservations for the specified venue
     */
    Page<Reservation> findAllByOrganizerIdAndVenueId(Long userId, Long venueId, Pageable pageable);
}
