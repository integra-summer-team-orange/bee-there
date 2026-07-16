package cloudflight.integra.backend.resources;

import cloudflight.integra.backend.resources.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    ResourceResponse toDto(Resource resource);

    @Mapping(target = "id", ignore = true)
    Resource toEntity(ResourceRequest dto);
}

