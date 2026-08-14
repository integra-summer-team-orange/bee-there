package cloudflight.integra.backend.authentication.model;

/**
 * Represents the data sent after successful log in
 */
public record LoginResponseDto(String token // user details?
        ) {}
