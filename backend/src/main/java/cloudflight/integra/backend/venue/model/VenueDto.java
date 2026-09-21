package cloudflight.integra.backend.venue.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record VenueDto(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        @Schema(
                description = "Id of the user managing this venue. Taken from the authenticated caller on"
                        + " creation and never changed afterwards.",
                accessMode = Schema.AccessMode.READ_ONLY)
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long managedBy,

        @Schema(description = "Display name of the user managing this venue.", accessMode = Schema.AccessMode.READ_ONLY)
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String managedByName,

        @NotBlank @Size(max = 150) String name,
        @Size(max = 255) String description,
        @NotBlank @Size(max = 255) String address,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY) @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime createdAt) {}
