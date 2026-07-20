package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.exceptions.UserNotFoundException;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.model.UserRequestDto;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(userService.getAll().stream().map(userMapper::toDto), HttpStatus.OK);
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return a response containing the requested user
     * @throws UserNotFoundException if no user with the specified identifier exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) throws UserNotFoundException {
        User user = userService.getById(id);

        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }

    /**
     * Creates a new user.
     *
     * @param dto the user data used to create the new user
     * @return a response containing the created user
     * @throws DuplicateEmailException if the email is not unique
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid UserRequestDto dto) throws DuplicateEmailException {
        User user = userService.create(userMapper.fromDto(dto));
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.CREATED);
    }

    /**
     * Updates an existing user.
     *
     * @param id the identifier of the user to update
     * @param userRequestDto the updated user data
     * @return a response containing the updated user
     * @throws DuplicateEmailException if the email is not unique
     * @throws UserNotFoundException if the user with the specified id does not exist
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid UserRequestDto userRequestDto)
        throws DuplicateEmailException, UserNotFoundException {

        User user = userService.update(id, userMapper.fromDto(userRequestDto));
        return new ResponseEntity<>(userMapper.toDto(user), HttpStatus.OK);
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the identifier of the user to delete
     * @return a response indicating that the user was successfully deleted
     * @throws UserNotFoundException if no user with the specified identifier exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws UserNotFoundException {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
