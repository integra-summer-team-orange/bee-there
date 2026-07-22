package cloudflight.integra.backend.user.model;

import cloudflight.integra.backend.user.validation.Phone;
import cloudflight.integra.backend.user.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents the data required to create or update a user.
 * Validation constraints ensure that all provided user information is valid.
 */
public record UserRequestDto(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email")
        String email,

        @StrongPassword String password,

        @NotBlank(message = "Phone is required") @Phone String phone,
        @NotNull(message = "Role is required") Role role) {}
