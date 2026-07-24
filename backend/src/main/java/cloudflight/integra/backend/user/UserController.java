package cloudflight.integra.backend.user;

import cloudflight.integra.backend.exceptions.ErrorResponse;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.model.UserRequestDto;
import cloudflight.integra.backend.user.model.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "API endpoints for managing users")
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
    @GetMapping
    @Operation(summary = "Retrieves all users", description = "Returns a response containing the list of all users")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Found users",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)),
                                    examples =
                                            @ExampleObject(
                                                    name = "Valid Response",
                                                    value = "[ { \"id\": 1, "
                                                            + "\"name\": \"Dan\", "
                                                            + "\"email\": \"dan@gmail.com\", "
                                                            + "\"phone\": \"0747474747\", "
                                                            + "\"role\": \"PARTICIPANT\", "
                                                            + "\"createdAt\": \"2026-07-21T10:00:00\" } ]"))
                        }),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Server Error",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 500, "
                                                            + "\"error\": \"Internal Server Error\", "
                                                            + "\"messages\": [\"An unexpected error occurred\"], "
                                                            + "\"path\": \"/api/users\" }"))
                        })
            })
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
    @Operation(
            summary = "Retrieves a user by its unique identifier",
            description = "Returns a response containing the requested user")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Found the user",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Valid Response",
                                                    value = "{ \"id\": 1, "
                                                            + "\"name\": \"Dan\", "
                                                            + "\"email\": \"dan@gmail.com\", "
                                                            + "\"phone\": \"0747474747\", "
                                                            + "\"role\": \"PARTICIPANT\", "
                                                            + "\"createdAt\": \"2026-07-21T10:00:00\" }"))
                        }),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid id supplied",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Bad Request",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 400, "
                                                            + "\"error\": \"Bad Request\", "
                                                            + "\"messages\": [\"Invalid id format\"], "
                                                            + "\"path\": \"/api/users/invalid-id\" }"))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Not Found",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 404, "
                                                            + "\"error\": \"Not Found\", "
                                                            + "\"messages\": [\"User not found\"], "
                                                            + "\"path\": \"/api/users/1\" }"))
                        }),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Server Error",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 500, "
                                                            + "\"error\": \"Internal Server Error\", "
                                                            + "\"messages\": [\"An unexpected error occurred\"], "
                                                            + "\"path\": \"/api/users/1\" }"))
                        })
            })
    public ResponseEntity<UserResponseDto> getById(
            @Parameter(description = "the identifier of the user to retrieve", example = "1") @PathVariable Long id) {
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
    @Operation(summary = "Creates a new user", description = "Returns a response containing the created user")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "User created successfully",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Valid Response",
                                                    value = "{ \"id\": 1, "
                                                            + "\"name\": \"Dan\", "
                                                            + "\"email\": \"dan@gmail.com\", "
                                                            + "\"phone\": \"0747474747\", "
                                                            + "\"role\": \"PARTICIPANT\", "
                                                            + "\"createdAt\": \"2026-07-21T10:00:00\" }"))
                        }),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Validation Error",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 400, "
                                                            + "\"error\": \"Bad Request\", "
                                                            + "\"messages\": [\"Name is required\", "
                                                            + "\"Invalid email format\"], "
                                                            + "\"path\": \"/api/users\" }"))
                        }),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Server Error",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 500, "
                                                            + "\"error\": \"Internal Server Error\", "
                                                            + "\"messages\": [\"An unexpected error occurred\"], "
                                                            + "\"path\": \"/api/users\" }"))
                        })
            })
    public ResponseEntity<UserResponseDto> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "UserRequestDto to create",
                            required = true,
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserRequestDto.class),
                                            examples =
                                                    @ExampleObject(
                                                            name = "Valid User",
                                                            value = "{ \"name\": \"Dan\", "
                                                                    + "\"email\": \"dan@gmail.com\", "
                                                                    + "\"password\": \"!DanIsAwesome123\", "
                                                                    + "\"phone\": \"0747474747\", "
                                                                    + "\"role\": \"PARTICIPANT\" }")))
                    @RequestBody
                    @Valid
                    UserRequestDto dto) {
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
    @Operation(summary = "Updates an existing user", description = "Returns a response containing the updated user")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User updated successfully",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Valid Response",
                                                    value = "{ \"id\": 1, "
                                                            + "\"name\": \"Dan\", "
                                                            + "\"email\": \"dan@gmail.com\", "
                                                            + "\"phone\": \"+40747474747\", "
                                                            + "\"role\": \"PARTICIPANT\", "
                                                            + "\"createdAt\": \"2026-07-21T10:00:00\" }"))
                        }),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Validation Error",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 400, "
                                                            + "\"error\": \"Bad Request\", "
                                                            + "\"messages\": [\"Name is required\"], "
                                                            + "\"path\": \"/api/users/1\" }"))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Not Found",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 404, "
                                                            + "\"error\": \"Not Found\", "
                                                            + "\"messages\": [\"User not found\"], "
                                                            + "\"path\": \"/api/users/1\" }"))
                        }),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Server Error",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 500, "
                                                            + "\"error\": \"Internal Server Error\", "
                                                            + "\"messages\": [\"An unexpected error occurred\"], "
                                                            + "\"path\": \"/api/users/1\" }"))
                        })
            })
    public ResponseEntity<UserResponseDto> update(
            @Parameter(description = "the identifier of the user to update", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            description = "UserRequestDto to update",
                            required = true,
                            content =
                                    @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = UserRequestDto.class),
                                            examples =
                                                    @ExampleObject(
                                                            name = "Valid User",
                                                            value = "{ \"name\": \"Dan\", "
                                                                    + "\"email\": \"dan@gmail.com\", "
                                                                    + "\"password\": \"!DanIsNotAwesome123\", "
                                                                    + "\"phone\": \"+40747474747\", "
                                                                    + "\"role\": \"PARTICIPANT\" }")))
                    @RequestBody
                    @Valid
                    UserRequestDto userRequestDto) {
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
    @Operation(
            summary = "Deletes a user by its unique identifier",
            description = "Returns a response indicating that the user was successfully deleted")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid input provided",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Bad Request",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 400, "
                                                            + "\"error\": \"Bad Request\", "
                                                            + "\"messages\": [\"Invalid id format\"], "
                                                            + "\"path\": \"/api/users/invalid-id\" }"))
                        }),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Not Found",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 404, "
                                                            + "\"error\": \"Not Found\", "
                                                            + "\"messages\": [\"User not found\"], "
                                                            + "\"path\": \"/api/users/1\" }"))
                        }),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples =
                                            @ExampleObject(
                                                    name = "Server Error",
                                                    value = "{ \"timestamp\": \"2026-07-21T10:00:00\", "
                                                            + "\"status\": 500, "
                                                            + "\"error\": \"Internal Server Error\", "
                                                            + "\"messages\": [\"An unexpected error occurred\"], "
                                                            + "\"path\": \"/api/users/1\" }"))
                        })
            })
    public ResponseEntity<Void> delete(
            @Parameter(description = "the identifier of the user to delete", example = "1") @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
