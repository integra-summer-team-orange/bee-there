package cloudflight.integra.backend.authentication;

import cloudflight.integra.backend.authentication.model.LoginRequestDto;
import cloudflight.integra.backend.authentication.model.LoginResponseDto;
import cloudflight.integra.backend.exceptions.ErrorResponse;
import cloudflight.integra.backend.user.UserMapper;
import cloudflight.integra.backend.user.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API endpoints for authentication")
public class AuthenticationController {

    private AuthenticationService service;
    private UserMapper mapper;

    /**
     * Constructs an {@code AuthenticationController} with the required services
     * for authentication operations and user DTO mapping.
     *
     * @param service the service responsible for registration and login operations
     * @param mapper the mapper responsible for converting between user entities and DTOs
     */
    public AuthenticationController(AuthenticationService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Registers a new user in the system.
     *
     * <p>The provided user request DTO is converted to a {@link User} entity,
     * which is then passed to the authentication service for registration.
     * The newly created user is converted back to a response DTO and returned
     * with HTTP status {@link HttpStatus#CREATED}.</p>
     *
     * @param dto the registration data provided by the client
     * @return a response containing the newly registered user
     */
    @PostMapping("/register")
    @Operation(
            summary = "register a new user into system",
            description = "the user provides all the data needed for account creation an the account is created")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "Successfully created an account",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid credentials",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRequestDto dto) {
        User user = service.register(mapper.fromDto(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(user));
    }

    /**
     * Authenticates a user using their email and password.
     *
     * <p>If the provided credentials are valid, the authentication service
     * generates a JWT token which is returned to the client.</p>
     *
     * @param dto the login credentials provided by the client
     * @return a response containing the generated JWT token
     */
    @PostMapping("/login")
    @Operation(
            summary = "Logs a user into account",
            description = "the user provides email and password and is logged in")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful login",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserResponseDto.class))),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid credentials",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class)))
            })
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        String token = service.login(dto.email(), dto.password());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
