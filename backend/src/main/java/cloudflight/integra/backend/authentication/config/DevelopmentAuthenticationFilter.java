package cloudflight.integra.backend.authentication.config;

import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Authenticates requests that arrive without a token as a fixed development account, so the screens can be
 * built before the login page exists.
 *
 * <p>RESTORE-AUTH: delete this class once login lands. Nothing else refers to it.
 */
@Component
@Profile("!test")
public class DevelopmentAuthenticationFilter extends OncePerRequestFilter {

    public static final String DEVELOPMENT_EMAIL = "dev@local";

    private static final Role DEVELOPMENT_ROLE = Role.ADMIN;

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentAuthenticationFilter.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DevelopmentAuthenticationFilter(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Only tokenless requests fall back, so an expired or malformed token still answers 401.
        boolean tokenless = request.getHeader("Authorization") == null;

        if (tokenless && SecurityContextHolder.getContext().getAuthentication() == null) {
            User developmentUser = loadOrCreate();

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(
                    "ROLE_" + developmentUser.getRole().name()));

            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(developmentUser, null, authorities));
        }

        filterChain.doFilter(request, response);
    }

    private User loadOrCreate() {
        User developmentUser = userRepository.findByEmail(DEVELOPMENT_EMAIL).orElseGet(this::create);

        // An earlier run of this branch may have left the account behind with a different role.
        if (developmentUser.getRole() != DEVELOPMENT_ROLE) {
            developmentUser.setRole(DEVELOPMENT_ROLE);
            developmentUser = userRepository.save(developmentUser);
        }

        return developmentUser;
    }

    private User create() {
        LOGGER.warn(
                "Authorization is switched off: requests without a token act as {}. See RESTORE-AUTH.",
                DEVELOPMENT_EMAIL);

        return userRepository.save(new User(
                "Dev User", DEVELOPMENT_EMAIL, passwordEncoder.encode("!Password123"), "0000000000", DEVELOPMENT_ROLE));
    }
}
