package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.model.UserRequestDto;
import cloudflight.integra.backend.user.model.UserResponseDto;
import org.mapstruct.Mapper;

/**
 * Maps between user entities and Data Transfer Objects (DTOs).
 */
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserMapper {

    /**
     * Converts a user request DTO into a user entity.
     *
     * @param userRequestDto the request DTO to convert
     * @return the mapped user entity
     */
    User fromDto(UserRequestDto userRequestDto);

    /**
     * Converts a user entity into a user response DTO.
     *
     * @param user the user entity to convert
     * @return the mapped user response DTO
     */
    UserResponseDto toDto(User user);
}
