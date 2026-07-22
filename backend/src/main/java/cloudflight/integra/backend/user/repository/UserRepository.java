package cloudflight.integra.backend.user.repository;

import cloudflight.integra.backend.user.model.User;

/**
 * Repository interface for managing {@link User} entities.
 * Provides CRUD operations for users identified with Long ID.
 */
public interface UserRepository extends RepositoryInterface<Long, User> {}
