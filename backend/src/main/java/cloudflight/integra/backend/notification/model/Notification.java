package cloudflight.integra.backend.notification.model;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private Long recipientId;
    private Long reservationId;
    private NotificationType type;
    private String message;
    private LocalDateTime sentAt;
    private boolean read;

    public Notification() {}

    public Notification(
            Long id,
            Long recipientId,
            Long reservationId,
            NotificationType type,
            String message,
            LocalDateTime sentAt,
            boolean read) {
        this.id = id;
        this.recipientId = recipientId;
        this.reservationId = reservationId;
        this.type = type;
        this.message = message;
        this.sentAt = sentAt;
        this.read = read;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
