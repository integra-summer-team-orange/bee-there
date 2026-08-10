package cloudflight.integra.backend.venue.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record VenueDto(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY) @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        Long id,

        Long managedBy,
        @NotBlank @Size(max = 150) String name,
        String description,
        @NotBlank String address,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY) @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        LocalDateTime createdAt) {}
