package cloudflight.integra.backend.reservation;

import cloudflight.integra.backend.reservation.model.Reservation;
import cloudflight.integra.backend.reservation.model.ReservationRequestDto;
import cloudflight.integra.backend.reservation.model.ReservationResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for converting between {@link Reservation} entities
 * and their respective Data Transfer Objects (DTOs).
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper {

    /**
     * Converts a reservation request DTO into a {@link Reservation} entity.
     *
     * @param dto the DTO containing the data required to create a reservation
     * @return the resulting {@link Reservation} entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "visibility", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "resourceId", target = "resource.id")
    @Mapping(source = "venueId", target = "venue.id")
    Reservation toEntity(ReservationRequestDto dto);

    /**
     * Converts a {@link Reservation} entity into a response DTO.
     *
     * @param reservation the entity to be mapped
     * @return the corresponding {@link ReservationResponseDto}
     */
    @Mapping(source = "resource.id", target = "resourceId")
    @Mapping(source = "venue.id", target = "venueId")
    @Mapping(source = "venue.name", target = "venueName")
    @Mapping(source = "organizer.id", target = "organizerId")
    ReservationResponseDto toDto(Reservation reservation);
}
