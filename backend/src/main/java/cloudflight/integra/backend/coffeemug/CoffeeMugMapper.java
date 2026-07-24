package cloudflight.integra.backend.coffeemug;

import cloudflight.integra.backend.coffee.CoffeeMapper;
import cloudflight.integra.backend.coffeemug.model.CoffeeMug;
import cloudflight.integra.backend.coffeemug.model.CoffeeMugDto;
import org.mapstruct.Mapper;

/**
 * Maps between {@link CoffeeMug} entities and {@link CoffeeMugDto} objects.
 */
@Mapper(componentModel = "spring", uses = CoffeeMapper.class)
public interface CoffeeMugMapper {

    /**
     * Converts a coffee mug entity to a data transfer object.
     *
     * @param mug the coffee mug entity to convert
     * @return the corresponding coffee mug data transfer object
     */
    CoffeeMugDto toDto(CoffeeMug mug);

    /**
     * Converts a coffee mug data transfer object to an entity.
     *
     * @param dto the coffee mug data transfer object to convert
     * @return the corresponding coffee mug entity
     */
    // TODO: add @Mapping(target = "coffee", ignore = true) when JPA is introduced
    CoffeeMug toEntity(CoffeeMugDto dto);
}
