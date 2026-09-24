package cloudflight.integra.backend.reservation.model;

import java.time.LocalDateTime;

public record ReservationResponseDto(
        Long id,
        Long resourceId,
        Long venueId,
        String venueName,
        Long organizerId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Status status,
        Integer maxParticipants,
        LocalDateTime createdAt) {}
