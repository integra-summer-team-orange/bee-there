package cloudflight.integra.backend.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jdk.jfr.Timestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the user information returned by the API.
 * Sensitive information such as the user's password is excluded from this response.
 */
public record UserResponseDto(
    UUID id,

    @NotBlank(message = "Name is required")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    String email,


    @NotBlank(message = "Phone is required")
    String phone,
    @NotBlank(message = "Role is required")
    Role role,

    @Timestamp
    LocalDateTime createdAt
) {
}
