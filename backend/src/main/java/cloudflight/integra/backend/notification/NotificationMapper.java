package cloudflight.integra.backend.notification;

import cloudflight.integra.backend.notification.model.Notification;
import cloudflight.integra.backend.notification.model.NotificationDto;
import org.mapstruct.Mapper;

/**
 * Mapper for converting {@link Notification} entities to {@link NotificationDto}s, and viceversa.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {
    /**
     * Converts a {@link Notification} entity to a {@link NotificationDto}.
     *
     * @param notification the entity to convert
     * @return the converted DTO
     */
    NotificationDto toDto(Notification notification);

    Notification toEntity(NotificationDto notificationDto);
}
