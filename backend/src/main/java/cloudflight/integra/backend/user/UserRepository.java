package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.model.User;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing user entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Optional<User> findByEmail(String email);
    // boolean existsByEmail(String email);
}
