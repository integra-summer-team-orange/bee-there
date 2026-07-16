package cloudflight.integra.backend.user;


import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.exceptions.RepositoryException;
import cloudflight.integra.backend.user.exceptions.UserNotFoundException;
import cloudflight.integra.backend.user.model.User;
import cloudflight.integra.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides business logic for managing users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all users.
     *
     * @return a list containing all users
     */
    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by its unique identifier.
     *
     * @param id the identifier of the user to retrieve
     * @return the requested user
     * @throws UserNotFoundException if no user with the specified identifier exists
     */
    public User getById(UUID id) throws RepositoryException {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException("User Not Found");
        }

        return user.get();
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return the created user
     * @throws DuplicateEmailException if the email is already used by another user
     */
    public User create(User user) throws RepositoryException {
        return userRepository.save(user);
    }

    /**
     * Updates an existing user.
     *
     * @param id the identifier of the user to update
     * @param user the updated user data
     * @return the updated user
     * @throws UserNotFoundException if no user with the specified identifier exists
     * @throws DuplicateEmailException if the email is already used by another user
     */
    public User update(UUID id, User user) throws RepositoryException {

        Optional<User> existing = userRepository.findById(id);

        if (existing.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        user.setId(id);
        user.setCreatedAt(existing.get().getCreatedAt());

        return userRepository.save(user);
    }

    /**
     * Deletes the user with the specified identifier.
     *
     * @param id the identifier of the user to delete
     * @throws UserNotFoundException if no user with the specified identifier exists
     */
    public void delete(UUID id) throws RepositoryException {
        userRepository.findById(id)
            .map(user -> {
                userRepository.deleteById(id);
                return true;
            })
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
