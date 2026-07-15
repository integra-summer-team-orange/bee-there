package cloudflight.integra.backend.venue.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VenueRequestDto(Long managedBy, @NotBlank @Size(max = 150) String name, String description, @NotBlank String address) {
}
