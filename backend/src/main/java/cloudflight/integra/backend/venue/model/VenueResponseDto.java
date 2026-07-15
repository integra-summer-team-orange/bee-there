package cloudflight.integra.backend.venue.model;
import java.time.LocalDateTime;

public record VenueResponseDto(Long id, Long managedBy, String name, String description, String address, LocalDateTime createdAt) {
}
