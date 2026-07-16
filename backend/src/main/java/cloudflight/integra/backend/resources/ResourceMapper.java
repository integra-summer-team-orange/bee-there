package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    /**
     * Converts a Resource entity to a ResourceResponse DTO.
     *
     * @param resource the resource entity
     * @return the resource response DTO
     */
    ResourceResponse toDto(Resource resource);


    /**
     * Converts a ResourceRequest DTO to a Resource entity.
     *
     * @param dto the resource request DTO
     * @return the resource entity
     */
    @Mapping(target = "id", ignore = true)
    Resource toEntity(ResourceRequest dto);
}

