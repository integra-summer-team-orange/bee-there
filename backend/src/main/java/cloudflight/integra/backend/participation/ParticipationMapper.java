package cloudflight.integra.backend.participation;

import cloudflight.integra.backend.participation.model.Participation;
import cloudflight.integra.backend.participation.model.ParticipationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for converting between {@link Participation} entities and {@link ParticipationDto} records.
 */
@Mapper(componentModel = "spring")
public interface ParticipationMapper {

    /**
     * Converts a {@link Participation} entity into a {@link ParticipationDto}.
     * A pending invite has no linked user, so {@code userId} is null and {@code email} carries the invited address.
     *
     * @param participation The entity to be mapped.
     * @return The corresponding {@link ParticipationDto}.
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "invitedEmail", target = "email")
    ParticipationDto toDto(Participation participation);

    /**
     * Converts a {@link ParticipationDto} into a {@link Participation} entity.
     * The ID is managed internally and ignored during mapping. The {@code user} is also ignored: the service
     * resolves it from {@code userId} or from the email, so a pending invite never gets a stub user.
     *
     * @param dto The DTO containing the data.
     * @return The resulting {@link Participation} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(source = "email", target = "invitedEmail")
    Participation toEntity(ParticipationDto dto);
}
