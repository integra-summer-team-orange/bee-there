package cloudflight.integra.backend.reservation.model;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record ReservationRequestDto(
        @NotNull Long resourceId,

        @NotNull Long venueId,

        @NotNull @Future LocalDateTime startTime,

        @NotNull @Future LocalDateTime endTime,

        @NotNull @Positive Integer maxParticipants) {}
