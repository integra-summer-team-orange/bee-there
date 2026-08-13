package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing user entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user
     * @return an {@code Optional} containing the user if found
     */
    Optional<User> findByEmail(String email);


    /**
     * Checks if there is a user with the email address
     *
     * @param email the email address of the user
     * @return an {@code boolean} whether it exists or not
     */
    boolean existsByEmail(String email);
}
