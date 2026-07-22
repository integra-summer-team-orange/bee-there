package cloudflight.integra.backend.notification;

import cloudflight.integra.backend.notification.model.Notification;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepository {
    private final Map<Long, Notification> notifications = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }

    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(notifications.get(id));
    }

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

    public void deleteById(Long id) {
        notifications.remove(id);
    }
}
