package cloudflight.integra.backend.user.repository;

import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.exceptions.RepositoryException;
import cloudflight.integra.backend.user.utils.UUIDGenerator;
import cloudflight.integra.backend.user.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of the {@link UserRepository} interface.
 * Stores users in a thread-safe map and generates unique UUIDs for new users.
 */
@Repository
public class UserMemoryRepository implements UserRepository {
    private final Map<UUID, User> users = new ConcurrentHashMap<>();
    private final UUIDGenerator generator;

    UserMemoryRepository(UUIDGenerator generator) {
        this.generator = generator;
    }

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
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * Saves a user in the repository. If the user does not already have an
     * identifier, a new UUID and creation timestamp are assigned.
     *
     * @param value the user to save
     * @return the saved user
     * @throws RepositoryException if a user with the same email already exists
     */
    @Override
    public User save(User value) throws RepositoryException {

        checkEmail(value.getEmail());

        if (value.getId() == null) {
            value.setId(generator.next(users.keySet()));
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
    public void deleteById(UUID id) {
        users.remove(id);
    }

    private void checkEmail(String email) throws DuplicateEmailException {
        if (users.values().stream()
            .anyMatch(user -> user.getEmail().equals(email))) {
            throw new DuplicateEmailException("There is already a user with this email.");
        }
    }
}
