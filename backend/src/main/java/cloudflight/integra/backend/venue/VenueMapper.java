package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import cloudflight.integra.backend.venue.model.VenueRequestDto;
import cloudflight.integra.backend.venue.model.VenueResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for converting between {@link Venue} entities and their respective Data Transfer Objects (DTOs).
 */
@Mapper(componentModel = "spring")
public interface VenueMapper {

    /**
     * Converts a request DTO into a {@link Venue} entity.
     * The ID and creation timestamp are managed internally by the application and are ignored during this mapping.
     *
     * @param venueRequestDto The DTO containing the incoming data for creating or updating a venue.
     * @return The resulting {@link Venue} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Venue toEntity(VenueRequestDto venueRequestDto);

    /**
     * Converts a {@link Venue} entity into a response DTO to be sent to the client.
     *
     * @param venue The entity to be mapped.
     * @return The corresponding {@link VenueResponseDto} containing the formatted data.
     */
    VenueResponseDto toResponseDto(Venue venue);
}
