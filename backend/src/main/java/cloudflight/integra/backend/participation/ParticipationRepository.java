package cloudflight.integra.backend.participation;

import cloudflight.integra.backend.participation.model.Participation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing participation entities.
 */
@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    List<Participation> findByReservationId(Long reservationId);

    List<Participation> findByInvitedEmailIgnoreCase(String email);

    boolean existsByReservationIdAndUserId(Long reservationId, Long userId);

    boolean existsByReservationIdAndInvitedEmailIgnoreCase(Long reservationId, String email);
}
