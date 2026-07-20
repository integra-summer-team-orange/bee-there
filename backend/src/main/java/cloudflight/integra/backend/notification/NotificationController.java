package cloudflight.integra.backend.notification;

import cloudflight.integra.backend.exceptions.ResourceNotFoundException;
import cloudflight.integra.backend.notification.model.NotificationDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing notifications.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService service;
    private final NotificationMapper mapper;

    /**
     * Constructs a new NotificationController.
     *
     * @param notificationService the service for managing notifications
     * @param notificationMapper  the mapper for converting between entities and DTOs
     */
    public NotificationController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.service = notificationService;
        this.mapper = notificationMapper;
    }

    /**
     * Retrieves all notifications.
     *
     * @return a list of all notifications.
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getAll() {
        List<NotificationDto> notifications = service.findAll().stream().map(mapper::toDto).toList();
        return ResponseEntity.ok(notifications);
    }

    /**
     * Retrieves a notification by its ID.
     *
     * @param id the ID of the notification to retrieve
     * @return the notification with the given ID
     * @throws ResourceNotFoundException if no notification is found with a matching ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getById(@PathVariable Long id) {
        return service.findById(id).map(mapper::toDto)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    /**
     * Creates a new notification.
     *
     * @param dto the DTO containing the notification data
     * @return the created notification
     */
    @PostMapping
    public ResponseEntity<NotificationDto> create(@Valid @RequestBody NotificationDto dto) {
        var notification = mapper.toEntity(dto);
        var created = service.create(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    /**
     * Marks a notification as read.
     *
     * @param id the ID of the notification to mark as read
     * @return the updated notification
     * @throws ResourceNotFoundException if no notification is found with a matching ID
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(@PathVariable Long id) {
        return service.markAsRead(id).map(mapper::toDto)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
    }

    /**
     * Deletes a notification by its ID.
     *
     * @param id the ID of the notification to delete
     * @throws ResourceNotFoundException if no notification is found with a matching ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.delete(id)) {
            throw new ResourceNotFoundException("Notification not found with id: " + id);
        }
        return ResponseEntity.noContent().build();
    }
}
