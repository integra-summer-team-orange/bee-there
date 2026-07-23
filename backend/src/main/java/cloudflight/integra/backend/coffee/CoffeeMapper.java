package cloudflight.integra.backend.coffee;

import cloudflight.integra.backend.coffee.model.Coffee;
import cloudflight.integra.backend.coffee.model.CoffeeDto;
import org.mapstruct.Mapper;

/**
 * Maps between {@link Coffee} entities and {@link CoffeeDto} objects.
 */
@Mapper(componentModel = "spring")
public interface CoffeeMapper {

    /**
     * Converts a coffee entity to a data transfer object.
     *
     * @param coffee the coffee entity to convert
     * @return the corresponding coffee data transfer object
     */
    CoffeeDto toDto(Coffee coffee);

    /**
     * Converts a coffee data transfer object to an entity.
     *
     * @param dto the coffee data transfer object to convert
     * @return the corresponding coffee entity
     */
    Coffee toEntity(CoffeeDto dto);
}
