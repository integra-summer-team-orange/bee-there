package cloudflight.integra.backend.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ResourceDto(
    Long id,

    @NotNull(message = "Venue ID is required")
    Long venueId,

    @NotBlank(message = "Name cannot be blank")
    String name,

    @NotBlank(message = "Activity type cannot be blank")
    String activityType,

    String activityDescription,

    @ValidResourceType
    String type,

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be a positive integer")
    Integer capacity,

    @NotNull(message = "Hourly rate is required")
    @PositiveOrZero(message = "Hourly rate must be zero or positive")
    BigDecimal hourlyRate
) {}
