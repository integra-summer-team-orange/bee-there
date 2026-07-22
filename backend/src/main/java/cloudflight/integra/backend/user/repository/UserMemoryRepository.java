package cloudflight.integra.backend.user.repository;

import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.model.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/**
 * In-memory implementation of the {@link UserRepository} interface.
 * Stores users in a thread-safe map and generates unique Longs for new users.
 */
@Repository
public class UserMemoryRepository implements UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Retrieves all users stored in the repository.
     *
     * @return a list containing all users
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return an {@code Optional} containing the user if found, or empty otherwise
     */
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Saves a user in the repository. If the user does not already have an
     * identifier, a new Long and creation timestamp are assigned.
     *
     * @param value the user to save
     * @return the saved user
     * @throws DuplicateEmailException if a user with the same email already exists
     */
    @Override
    public User save(User value) throws DuplicateEmailException {

        checkEmail(value);

        if (value.getId() == null) {
            value.setId(idGen.getAndIncrement());
            value.setCreatedAt(LocalDateTime.now());
        }

        users.put(value.getId(), value);
        return value;
    }

    /**
     * Removes the user with the specified identifier from the repository.
     *
     * @param id the identifier of the user to remove
     */
    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    private void checkEmail(User user) throws DuplicateEmailException {
        boolean exists = users.values().stream()
                .anyMatch(existing -> existing.getEmail().equals(user.getEmail())
                        && !existing.getId().equals(user.getId()));

        if (exists) {
            throw new DuplicateEmailException("There is already a user with this email.");
        }
    }
}
