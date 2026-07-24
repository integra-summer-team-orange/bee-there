package cloudflight.integra.backend.user;

import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.model.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User save(User value) {
        checkEmail(value);

        if (value.getId() == null) {
            value.setId(idGen.getAndIncrement());
            value.setCreatedAt(LocalDateTime.now());
        }

        users.put(value.getId(), value);
        return value;
    }

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
