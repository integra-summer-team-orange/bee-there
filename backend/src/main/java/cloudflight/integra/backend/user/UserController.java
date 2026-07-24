package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.model.UserRequestDto;
import cloudflight.integra.backend.user.model.UserResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes CRUD operations for managing users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Retrieves all users.
     *
     * @return a response containing the list of all users
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll() {
        return ResponseEntity.ok(
                userService.getAll().stream().map(userMapper::toDto).toList());
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return a response containing the requested user
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Creates a new user.
     *
     * @param dto the user data used to create the new user
     * @return a response containing the created user
     */
    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody @Valid UserRequestDto dto) {
        User user = userService.create(userMapper.fromDto(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
    }

    /**
     * Updates an existing user.
     *
     * @param id the identifier of the user to update
     * @param userRequestDto the updated user data
     * @return a response containing the updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(
            @PathVariable Long id, @RequestBody @Valid UserRequestDto userRequestDto) {
        User user = userService.update(id, userMapper.fromDto(userRequestDto));
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the identifier of the user to delete
     * @return a response indicating that the user was successfully deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
