package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import cloudflight.integra.backend.resources.model.ResourceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface responsible for converting between {@link Resource} entities and {@link ResourceDto} records.
 */
@Mapper(componentModel = "spring")
public interface ResourceMapper {

    /**
     * Converts a {@link Resource} entity into a {@link ResourceDto}.
     *
     * @param resource The entity to be mapped.
     * @return The corresponding {@link ResourceDto}.
     */
    @Mapping(source = "venue.id", target = "venueId")
    ResourceDto toDto(Resource resource);

    /**
     * Converts a {@link ResourceDto} into a {@link Resource} entity.
     * The ID is managed internally and ignored during mapping. The {@code venueId} is mapped
     * onto a stub {@link cloudflight.integra.backend.venue.model.Venue} reference carrying only
     * its ID — the database enforces whether it actually refers to an existing venue.
     *
     * @param dto The DTO containing the data.
     * @return The resulting {@link Resource} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "venueId", target = "venue.id")
    Resource toEntity(ResourceDto dto);
}
