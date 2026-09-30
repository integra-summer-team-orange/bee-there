package cloudflight.integra.backend.participation.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "Data Transfer Object for a reservation participation")
public record ParticipationDto(
        @Schema(description = "Unique identifier of the participation", accessMode = Schema.AccessMode.READ_ONLY)
                Long id,
        @Schema(
                        description = "ID of the reservation this participation belongs to",
                        accessMode = Schema.AccessMode.READ_ONLY,
                        example = "1")
                Long reservationId,
        @Schema(
                        description = "ID of an existing user to invite, or the participating user once linked",
                        example = "3")
                Long userId,
        @Email(message = "Invalid email")
                @Schema(
                        description = "Email to invite when the person has no account yet, or the pending invite email",
                        example = "guest@example.com")
                String email,
        @Schema(description = "Status of the participation", example = "INVITED") ParticipationStatus status) {}
