package cloudflight.integra.backend.user.model;

import java.time.LocalDateTime;

/**
 * Represents the user information returned by the API.
 * Sensitive information such as the user's password is excluded from this response.
 */
public record UserResponseDto(Long id, String name, String email, String phone, Role role, LocalDateTime createdAt) {}
