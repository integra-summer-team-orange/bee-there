package cloudflight.integra.backend.user.model;

import cloudflight.integra.backend.user.validation.Phone;
import cloudflight.integra.backend.user.validation.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InviteUserRequestDto(
        @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Email is required") @Email(message = "Invalid email")
        String email,

        @NotBlank(message = "Phone is required") @Phone String phone,

        @NotBlank(message = "Role is required") @UserRole String role) {}
