package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.model.InviteUserRequestDto;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.model.UserRequestDto;
import cloudflight.integra.backend.user.model.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps between user entities and Data Transfer Objects (DTOs).
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converts a user request DTO into a user entity.
     *
     * @param userRequestDto the request DTO to convert
     * @return the mapped user entity
     */
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User fromDto(UserRequestDto userRequestDto);

    /**
     * Converts a user entity into a user response DTO.
     *
     * @param user the user entity to convert
     * @return the mapped user response DTO
     */
    UserResponseDto toDto(User user);

    /**
     * Converts an invitation user request DTO into a user entity.
     *
     * @param inviteUserRequestDto the request DTO to convert
     * @return the mapped user entity
     */
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User fromDto(InviteUserRequestDto inviteUserRequestDto);
}
