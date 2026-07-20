package cloudflight.integra.backend.venue.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record VenueDto(Long id, Long managedBy, @NotBlank @Size(max = 150) String name, String description, @NotBlank String address, LocalDateTime createdAt) {
}
