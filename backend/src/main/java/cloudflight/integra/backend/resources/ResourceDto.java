package cloudflight.integra.backend.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Schema(description = "Data Transfer Object for Resource")
public record ResourceDto(
    @Schema(description = "Unique identifier of the resource", accessMode = Schema.AccessMode.READ_ONLY)
    Long id,

    @NotNull(message = "Venue ID is required")
    @Schema(description = "ID of the venue this resource belongs to", example = "1")
    Long venueId,

    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "Name of the resource", example = "Badminton Court 1")
    String name,

    @NotBlank(message = "Activity type cannot be blank")
    @Schema(description = "Type of activity", example = "Badminton")
    String activityType,

    @Schema(description = "Detailed description of the activity", example = "A full-sized badminton court")
    String activityDescription,

    @ValidResourceType
    @Schema(
        description = "Type of resource",
        allowableValues = {"INDOOR_SPORT", "OUTDOOR_SPORT", "BOARDGAME_SOCIAL"},
        example = "INDOOR_SPORT")
    String type,

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be a positive integer")
    @Schema(description = "Maximum capacity of the resource", example = "4")
    Integer capacity,

    @NotNull(message = "Hourly rate is required")
    @PositiveOrZero(message = "Hourly rate must be zero or positive")
    @Schema(description = "Hourly rental rate of the resource", example = "15.00")
    BigDecimal hourlyRate) {}
