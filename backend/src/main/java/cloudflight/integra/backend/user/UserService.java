package cloudflight.integra.backend.user;

import cloudflight.integra.backend.exceptions.EntityNotFoundException;
import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Provides business logic for managing users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user service
     *
     * @param userRepository the user repository
     * @param passwordEncoder bcrypt password encoder
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves a paginated list of users.
     *
     * @param page the page index to retrieve (zero-based)
     * @param size the number of users to include on each page
     * @return a {@code Page} containing the users for the requested page
     */
    public Page<User> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
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
     * @throws EntityNotFoundException if no user with the specified identifier exists
     */
    public User getById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new EntityNotFoundException("User Not Found");
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
    public User create(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    /**
     * Updates an existing user.
     *
     * @param id the identifier of the user to update
     * @param user the updated user data
     * @return the updated user
     * @throws EntityNotFoundException if no user with the specified identifier exists
     * @throws DuplicateEmailException if the email is already used by another user
     */
    public User update(Long id, User user) {

        Optional<User> existing = userRepository.findById(id);

        if (existing.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }

        user.setId(id);
        user.setCreatedAt(existing.get().getCreatedAt());
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        return userRepository.save(user);
    }

    /**
     * Deletes the user with the specified identifier.
     *
     * @param id the identifier of the user to delete
     * @throws EntityNotFoundException if no user with the specified identifier exists
     */
    public void delete(Long id) {
        userRepository
                .findById(id)
                .map(user -> {
                    userRepository.deleteById(id);
                    return true;
                })
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
