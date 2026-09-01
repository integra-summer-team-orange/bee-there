package cloudflight.integra.backend.user;

import cloudflight.integra.backend.exceptions.ErrorResponse;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.model.UserRequestDto;
import cloudflight.integra.backend.user.model.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes CRUD operations for managing users.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for creating, retrieving, updating, and deleting users.")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * Creates a new user controller.
     *
     * @param userService the user service
     * @param userMapper the user mapper
     */
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * Retrieves all users.
     *
     * @return a response containing the list of all users
     */
    @Operation(summary = "List of users", description = "Returns a list of users stored in the system.")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAll().stream().map(userMapper::toDto).toList());
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param pageNumber the zero-based page index to retrieve
     * @param pageSize   the maximum number of items to return per page
     * @return A {@link ResponseEntity} containing the requested page of {@link UserResponseDto} with a 200 OK status.
     */
    @GetMapping(
            path = "/paged",
            params = {"pageNumber", "pageSize"})
    @Operation(
            summary = "Gets a page of users",
            description = "Returns a paginated list of users stored in the system.")
    @ApiResponse(responseCode = "200", description = "Paginated users retrieved successfully")
    public ResponseEntity<Page<UserResponseDto>> getAllUsersPaginated(
            @Parameter(description = "Number of the desired page (0-based index)", example = "0", required = true)
                    @RequestParam
                    int pageNumber,
            @Parameter(description = "Size of page", example = "10", required = true) @RequestParam int pageSize) {
        return ResponseEntity.ok(userService.getAll(pageNumber, pageSize).map(userMapper::toDto));
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return a response containing the requested user
     */
    @Operation(summary = "Get a user by ID", description = "Retrieves a single user by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User found",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponseDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @Parameter(description = "ID of the user to be retrieved", required = true) @PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Creates a new user.
     *
     * @param dto the user data used to create the new user
     * @return a response containing the created user
     */
    @Operation(summary = "Create a new user", description = "Adds a new user to the system.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "User successfully created",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto dto) {
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
    @Operation(summary = "Update an existing user", description = "Updates the user details for the given ID.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User successfully updated",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponseDto.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input data",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @Parameter(description = "ID of the user to be updated", required = true) @PathVariable Long id,
            @RequestBody @Valid UserRequestDto userRequestDto) {
        User user = userService.update(id, userMapper.fromDto(userRequestDto));
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the identifier of the user to delete
     * @return a response indicating that the user was successfully deleted
     */
    @Operation(summary = "Delete a user", description = "Removes a user by its unique identifier.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "User successfully deleted", content = @Content),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to be deleted", required = true) @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
