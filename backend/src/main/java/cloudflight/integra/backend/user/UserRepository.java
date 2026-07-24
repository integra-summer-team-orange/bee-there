package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.model.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing user entities.
 */
@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all users.
     *
     * @return a list of all users
     */
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return an optional containing the requested user if found, otherwise an empty optional
     */
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Saves a user.
     *
     * <p>If the user does not yet have an identifier, a new one is generated and the
     * creation timestamp is set.</p>
     *
     * @param value the user to save
     * @return the saved user
     * @throws DuplicateEmailException if another user with the same email already exists
     */
    public User save(User value) {
        checkEmail(value);

        if (value.getId() == null) {
            value.setId(idGen.getAndIncrement());
            value.setCreatedAt(LocalDateTime.now());
        }

        users.put(value.getId(), value);
        return value;
    }

    /**
     * Deletes a user by its unique identifier.
     *
     * @param id the identifier of the user to delete
     */
    public void deleteById(Long id) {
        users.remove(id);
    }

    private void checkEmail(User user) {
        boolean exists = users.values().stream()
                .anyMatch(existing -> existing.getEmail().equals(user.getEmail())
                        && !existing.getId().equals(user.getId()));

        if (exists) {
            throw new DuplicateEmailException("There is already a user with this email.");
        }
    }
}
