package cloudflight.integra.backend.venue;

import cloudflight.integra.backend.venue.model.Venue;
import cloudflight.integra.backend.venue.model.VenueRequestDto;
import cloudflight.integra.backend.venue.model.VenueResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Venue toEntity(VenueRequestDto venueRequestDto);

    VenueResponseDto toResponseDto(Venue venue);
}
