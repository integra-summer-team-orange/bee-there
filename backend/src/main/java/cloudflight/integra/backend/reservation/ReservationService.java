package cloudflight.integra.backend.reservation;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.reservation.model.Reservation;
import cloudflight.integra.backend.reservation.model.Status;
import java.util.Objects;
import java.util.Optional;

import cloudflight.integra.backend.resources.ResourceService;
import cloudflight.integra.backend.resources.model.Resource;
import cloudflight.integra.backend.venue.VenueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing reservations.
 */
@Service
public class ReservationService {
    //TODO::add check if entities exist
    private final ReservationRepository reservationRepository;
    private final VenueService venueService;
    private final ResourceService resourceService;

    /**
     * Creates a new reservation service.
     *
     * @param reservationRepository the repository used to access reservation data
     * @param venueService the service for venues
     * @param resourceService the service for resources
     */
    public ReservationService(ReservationRepository reservationRepository, VenueService venueService, ResourceService resourceService) {
        this.reservationRepository = reservationRepository;
        this.venueService = venueService;
        this.resourceService = resourceService;
    }

    /**
     * Retrieves a paginated list of reservations belonging to the specified organizer.
     * Optionally filters the reservations by venue.
     *
     * @param page the page index to retrieve (zero-based)
     * @param size the number of reservations to include on each page
     * @param userId the identifier of the organizer whose reservations are retrieved
     * @param venueId the optional identifier of the venue used to filter reservations
     * @return a {@code Page} containing the reservations for the requested page
     */
    public Page<Reservation> getAll(int page, int size, Long userId, Long venueId) {

        Pageable pageable = PageRequest.of(page, size);

        if (venueId != null) {
            return reservationRepository.findAllByOrganizerIdAndVenueId(userId, venueId, pageable);
        }

        return reservationRepository.findAllByOrganizerId(userId, pageable);
    }

    /**
     * Retrieves a reservation by its unique identifier.
     *
     * @param id the identifier of the reservation
     * @return the reservation with the specified identifier
     * @throws EntityNotFoundException if no reservation exists with the specified identifier
     */
    public Reservation getById(Long id) {
        return reservationRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation with id: " + id + " not found!"));
    }

    /**
     * Creates a new reservation after validating that no overlapping reservation
     * exists for the same resource.
     *
     * @param reservation the reservation to create
     * @return the created reservation
     * @throws IllegalArgumentException if an overlapping reservation already exists
     */
    public Reservation create(Reservation reservation) {

        if (reservationRepository.existsOverlappingReservation(
                reservation.getResource().getId(),
                Status.CANCELLED,
                reservation.getStartTime(),
                reservation.getEndTime())) {
            throw new IllegalArgumentException("There is already a reservation for this resource in that time slot");
        }

        return reservationRepository.save(reservation);
    }

    /**
     * Cancels a reservation if the specified user is its organizer.
     * The reservation is not physically deleted; its status is changed to {@link Status#CANCELLED}.
     *
     * @param id the identifier of the reservation to cancel
     * @param userId the identifier of the user attempting to cancel the reservation
     * @throws EntityNotFoundException if no reservation exists with the specified identifier
     * @throws AccessDeniedException if the specified user is not the organizer of the reservation
     */
    public void delete(Long id, Long userId) {
        Optional<Reservation> reservation = reservationRepository.findById(id);

        if (reservation.isEmpty()) {
            throw new EntityNotFoundException("Reservation with id: " + id + " not found!");
        }

        if (!Objects.equals(reservation.get().getOrganizer().getId(), userId)) {
            throw new AccessDeniedException("You are not allowed to manage this reservation");
        }

        reservation.get().setStatus(Status.CANCELLED);
        reservationRepository.save(reservation.get());
    }
}
