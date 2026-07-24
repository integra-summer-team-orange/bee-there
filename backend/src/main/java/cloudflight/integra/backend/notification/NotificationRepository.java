package cloudflight.integra.backend.notification;

import cloudflight.integra.backend.notification.model.Notification;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing notification entities.
 */
@Repository
public class NotificationRepository {
    private final Map<Long, Notification> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all notification entries.
     *
     * @return a list of all notification entries
     */
    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }

    /**
     * Retrieves a notification by its unique identifier.
     *
     * @param id the identifier of the notification to retrieve
     * @return an optional containing the requested notification if found, otherwise an empty optional
     */
    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(notifications.get(id));
    }

    /**
     * Saves a notification entry.
     *
     * <p>If the notification does not yet have an identifier, a new one is generated.
     * If the notification does not have a sent timestamp, the current date and time is assigned.</p>
     *
     * @param notification the notification to save
     * @return the saved notification entry
     */
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(idGen.getAndIncrement());
        }
        if (notification.getSentAt() == null) {
            notification.setSentAt(java.time.LocalDateTime.now());
        }
        notifications.put(notification.getId(), notification);
        return notification;
    }

    /**
     * Deletes a notification by its unique identifier.
     *
     * @param id the identifier of the notification to delete
     */
    public void deleteById(Long id) {
        notifications.remove(id);
    }
}
