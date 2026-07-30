package cloudflight.integra.backend.notification;

import cloudflight.integra.backend.notification.model.Notification;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing notification entities.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
