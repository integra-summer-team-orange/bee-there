package cloudflight.integra.backend.participation;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.participation.model.Participation;
import cloudflight.integra.backend.participation.model.ParticipationStatus;
import cloudflight.integra.backend.user.UserService;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Service responsible for managing reservation participations and email invites.
 */
@Service
public class ParticipationService {

    private final ParticipationRepository repository;
    private final UserService userService;

    /**
     * Creates a new participation service.
     *
     * @param repository the participation repository
     * @param userService the service used to look up users
     */
    public ParticipationService(ParticipationRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    /**
     * Invites a person to a reservation, either by user ID or by email.
     * If the email belongs to an existing user, the participation is linked to that user directly;
     * otherwise a pending invite holding only the email is stored until the person registers.
     *
     * @param reservationId the reservation the person is invited to
     * @param userId the ID of an existing user to invite, or {@code null}
     * @param email the email to invite, or {@code null}
     * @param currentUser the user sending the invite
     * @return the created participation
     * @throws IllegalArgumentException if not exactly one of {@code userId} and {@code email} is given,
     * or if the person is already invited
     * @throws AccessDeniedException if the current user may not invite to this reservation
     * @throws EntityNotFoundException if {@code userId} does not refer to an existing user
     */
    public Participation invite(Long reservationId, Long userId, String email, User currentUser) {
        checkCanManage(reservationId, currentUser);

        boolean hasEmail = email != null && !email.isBlank();
        if ((userId == null) == !hasEmail) {
            throw new IllegalArgumentException("Provide either userId or email, not both");
        }

        Participation participation = new Participation();
        participation.setReservationId(reservationId);
        participation.setStatus(ParticipationStatus.INVITED);

        if (userId != null) {
            participation.setUser(userService.getById(userId));
        } else {
            String normalizedEmail = email.trim();
            if (userService.existsByEmail(normalizedEmail)) {
                participation.setUser(userService.loadUserByEmail(normalizedEmail));
            } else {
                participation.setInvitedEmail(normalizedEmail);
            }
        }

        checkNotAlreadyInvited(participation);
        return repository.save(participation);
    }

    /**
     * Links every pending invite sent to the new user's email to their account.
     * Called right after a user registers.
     *
     * @param newUser the freshly registered user
     */
    public void convertPendingInvites(User newUser) {
        List<Participation> pending = repository.findByInvitedEmailIgnoreCase(newUser.getEmail());
        if (pending.isEmpty()) {
            return;
        }
        pending.forEach(participation -> {
            participation.setUser(newUser);
            participation.setInvitedEmail(null);
        });
        repository.saveAll(pending);
    }

    /**
     * Lists the participations of a reservation.
     *
     * @param reservationId the reservation to list participations for
     * @param currentUser the user requesting the list
     * @return the participations of the reservation
     * @throws AccessDeniedException if the current user is neither a participant nor allowed to manage the reservation
     */
    public List<Participation> listForReservation(Long reservationId, User currentUser) {
        if (!canManage(currentUser) && !repository.existsByReservationIdAndUserId(reservationId, currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to view the participants of this reservation");
        }
        return repository.findByReservationId(reservationId);
    }

    /**
     * Lets an invited user accept or decline their own invite.
     *
     * @param participationId the participation to update
     * @param status the new status, must be {@code ACCEPTED} or {@code DECLINED}
     * @param currentUser the user answering the invite
     * @return the updated participation
     * @throws IllegalArgumentException if the status is missing or {@code INVITED}
     * @throws EntityNotFoundException if the participation does not exist
     * @throws AccessDeniedException if the participation belongs to someone else
     */
    public Participation updateStatus(Long participationId, ParticipationStatus status, User currentUser) {
        if (status == null || status == ParticipationStatus.INVITED) {
            throw new IllegalArgumentException("Status must be ACCEPTED or DECLINED");
        }

        Participation participation = repository
                .findById(participationId)
                .orElseThrow(() -> new EntityNotFoundException("Participation not found"));

        if (participation.getUser() == null
                || !participation.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to answer this invite");
        }

        participation.setStatus(status);
        return repository.save(participation);
    }

    private void checkNotAlreadyInvited(Participation participation) {
        Long reservationId = participation.getReservationId();
        boolean duplicate = participation.getUser() != null
                ? repository.existsByReservationIdAndUserId(
                        reservationId, participation.getUser().getId())
                : repository.existsByReservationIdAndInvitedEmailIgnoreCase(
                        reservationId, participation.getInvitedEmail());
        if (duplicate) {
            throw new IllegalArgumentException("This person is already invited to the reservation");
        }
    }


    // reservationService.getById(reservationId).getOrganizer().getId().equals(currentUser.getId())
    private void checkCanManage(Long reservationId, User currentUser) {
        if (!canManage(currentUser)) {
            throw new AccessDeniedException("You are not allowed to manage this reservation");
        }
    }

    private boolean canManage(User currentUser) {
        return currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.VENUE_ADMIN;
    }
}
