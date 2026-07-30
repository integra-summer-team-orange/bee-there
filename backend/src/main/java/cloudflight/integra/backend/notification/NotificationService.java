package cloudflight.integra.backend.notification;

import cloudflight.integra.backend.notification.model.Notification;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing notifications.
 * Provides methods to perform CRUD operations on {@link Notification} entities,
 * such as retrieving, creating, updating, deleting, and marking notifications as read.
 */
@Service
public class NotificationService {
    private final NotificationRepository repository;
    // todo: user and reservation-specific services to be added here

    /**
     * Constructs a new instance of the NotificationService.
     *
     * @param repository the repository used for performing CRUD operations on notifications
     */
    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }


    /**
     * Retrieves a page of notification entities with specific page number and size
     * @param page page number
     * @param size page size
     * @return a page containing the requested notifications
     */
    public Page<Notification> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page,size);
        return repository.findAll(pageable);

    }

    /**
     * Retrieves all notifications.
     *
     * @return a list of all notifications
     */
    public List<Notification> findAll() {
        return repository.findAll();
    }

    /**
     * Retrieves a notification by its ID.
     *
     * @param id the ID of the notification to retrieve
     * @return an optional containing the notification if found, empty otherwise
     */
    public Optional<Notification> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Creates a new notification.
     *
     * @param notification the notification to create
     * @return the created notification
     * @throws IllegalArgumentException if the message is blank or exceeds 500 characters,
     * or if the notification type is null
     */
    public Notification create(Notification notification) {
        return repository.save(notification);
    }

    /**
     * Updates an existing notification.
     *
     * @param id the ID of the notification to update
     * @param notification the updated notification data
     * @return an optional containing the updated notification if found, empty otherwise
     */
    public Optional<Notification> update(Long id, Notification notification) {
        return repository.findById(id).map(existing -> {
            notification.setId(id);
            return repository.save(notification);
        });
    }

    /**
     * Marks a notification as read.
     *
     * @param id the ID of the notification to mark as read
     * @return an optional containing the updated notification if found, empty otherwise
     */
    public Optional<Notification> markAsRead(Long id) {
        return repository.findById(id).map(existing -> {
            existing.setRead(true);
            return repository.save(existing);
        });
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param id the ID of the notification to delete
     * @return true if the notification was deleted, false otherwise
     */
    public boolean delete(Long id) {
        return repository
                .findById(id)
                .map(existing -> {
                    repository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
}
