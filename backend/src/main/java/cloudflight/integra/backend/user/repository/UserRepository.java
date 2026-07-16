package cloudflight.integra.backend.user.repository;

import cloudflight.integra.backend.user.model.User;

import java.util.UUID;

/**
 * Repository interface for managing {@link User} entities.
 * Provides CRUD operations for users identified by a UUID.
 */
public interface UserRepository extends RepositoryInterface<UUID, User> {
}
