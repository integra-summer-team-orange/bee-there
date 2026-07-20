package cloudflight.integra.backend.notification.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Notifications.
 */
public record NotificationDto(Long id, @NotNull(message = "Recipient ID is required") Long recipientId, Long reservationId, @NotNull(message = "Notification type is required") NotificationType type, @NotBlank(message = "Message cannot be blank") @Size(max = 500, message = "Message must be under 500 characters") String message, LocalDateTime sentAt, boolean read) { }
