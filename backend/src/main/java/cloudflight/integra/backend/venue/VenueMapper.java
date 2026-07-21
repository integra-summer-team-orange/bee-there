package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import cloudflight.integra.backend.venue.model.VenueDto;
import org.mapstruct.Mapper;

/**
 * Mapper interface responsible for converting between {@link Venue} entities and their Data Transfer Objects (DTOs).
 */
@Mapper(componentModel = "spring")
public interface VenueMapper {

    /**
     * Converts a DTO into a {@link Venue} entity.
     * The ID and creation timestamp are managed internally by the application and are ignored during this mapping
     * to prevent client manipulation.
     *
     * @param venueDto The DTO containing the incoming data for creating or updating a venue.
     * @return The resulting {@link Venue} entity.
     */
    Venue toEntity(VenueDto venueDto);

    /**
     * Converts a {@link Venue} entity into a DTO to be sent to the client.
     *
     * @param venue The entity to be mapped.
     * @return The corresponding {@link VenueDto} containing the formatted data.
     */
    VenueDto toDto(Venue venue);
}
