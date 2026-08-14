package cloudflight.integra.backend.authentication;

import cloudflight.integra.backend.user.UserService;
import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Provides business logic for user authentication.
 */
@Service
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Creates a new authentication service.
     *
     * @param userService the user service
     * @param passwordEncoder the password encoder used to verify passwords
     * @param jwtService the service used to generate JWT tokens
     */
    public AuthenticationService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Registers a new user.
     *
     * @param user the user to register
     * @throws DuplicateEmailException if there is already an email registered
     * @return the registered user
     */
    public User register(User user) {
        if (userService.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Email already in use");
        }
        return userService.create(user);
    }

    /**
     * Authenticates a user using their email and password.
     *
     * @param email the email of the user
     * @param password the password provided by the user
     * @return a JWT token for the authenticated user
     * @throws IllegalArgumentException if the provided password does not match
     *         the stored password
     */
    public String login(String email, String password) {
        User user = userService.loadUserByEmail(email);

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return jwtService.generateToken(user);
    }
}
