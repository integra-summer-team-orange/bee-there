package cloudflight.integra.backend.user.model;


import cloudflight.integra.backend.user.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents the data required to log in a user
 */
public record UserLoginDto (
        @NotBlank(message = "Email is required") @Email(message = "Invalid email")
        String email,
        @StrongPassword
        String password
){}
