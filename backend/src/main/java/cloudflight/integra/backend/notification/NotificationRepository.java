package cloudflight.integra.backend.notification;

import cloudflight.integra.backend.notification.model.Notification;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repository class for managing notifications.
 * Provides CRUD operations to handle Notification objects.
 * This class operates as an in-memory data store using a thread-safe
 * {@link AtomicLong} for ID generation and a {@link HashMap} for storage.
 */
@Repository
public class NotificationRepository {
    private final Map<Long, Notification> notifications =  new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all notifications currently stored in the repository.
     *
     * @return a list of all {@code Notification} instances.
     */
    public List<Notification> findAll() { return new ArrayList<>(notifications.values());    }

    /**
     * Finds a notification by its ID.
     *
     * @param id the ID of the notification to retrieve
     * @return an {@code Optional} containing the notification if found, or an empty {@code Optional} if no notification exists with the given ID
     */
    public Optional<Notification> findById(Long id) { return Optional.ofNullable(notifications.get(id)); }

    /**
     * Saves a {@link Notification} to the repository. If the notification does not have an ID,
     * a new unique ID will be generated and assigned to it before saving.
     *
     * @param notification the {@code Notification} object to be saved. It can be a new notification or an update to
     *                      an existing one.
     * @return the saved {@code Notification} with its ID set if it was previously null.
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
     * Deletes a {@link Notification} from the repository, based on its ID.
     * @param id the ID of the notification to delete
     */
    public void deleteById(Long id) { notifications.remove(id); }
}
