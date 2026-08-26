package cloudflight.integra.backend.authentication.config;

import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the account that requests without a token are attributed to, and hands it to {@link SecurityUtils}.
 *
 * <p>RESTORE-AUTH: this whole class exists only because there is no login screen yet. Delete it, along with
 * the development-user branch in {@link SecurityUtils}, once login lands.
 *
 * <p>It is skipped under the {@code test} profile so integration tests keep full control of the user table.
 */
@Component
@Profile("!test")
public class DevelopmentUserSeeder implements ApplicationRunner {

    /** Address the development account is looked up by. Its id is whatever the database assigns. */
    public static final String DEVELOPMENT_EMAIL = "dev@local";

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentUserSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates the seeder.
     *
     * @param userRepository repository used to look up or create the development account
     * @param passwordEncoder encoder used to hash the development password
     */
    public DevelopmentUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        User developmentUser = userRepository.findByEmail(DEVELOPMENT_EMAIL).orElseGet(this::create);

        SecurityUtils.setDevelopmentUser(developmentUser);

        LOGGER.warn(
                "Authorization is switched off: requests without a token act as {} (id {}). See RESTORE-AUTH.",
                DEVELOPMENT_EMAIL,
                developmentUser.getId());
    }

    private User create() {
        User user = new User(
                "Local Development",
                DEVELOPMENT_EMAIL,
                passwordEncoder.encode("!Password123"),
                "0000000000",
                Role.VENUE_ADMIN);

        return userRepository.save(user);
    }
}
