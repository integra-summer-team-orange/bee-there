package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import cloudflight.integra.backend.venue.model.VenueRequestDto;
import cloudflight.integra.backend.venue.model.VenueResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VenueMapper {
    Venue toEntity(VenueRequestDto venueRequestDto);
    VenueResponseDto toResponseDto(Venue venue);
}
