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
    ResourceDto toDto(Resource resource);

    /**
     * Converts a {@link ResourceDto} into a {@link Resource} entity.
     * The ID is managed internally and ignored during mapping.
     *
     * @param dto The DTO containing the data.
     * @return The resulting {@link Resource} entity.
     */
    @Mapping(target = "id", ignore = true)
    Resource toEntity(ResourceDto dto);
}
