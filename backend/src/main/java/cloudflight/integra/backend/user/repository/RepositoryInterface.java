package cloudflight.integra.backend.user.repository;

import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import java.util.List;
import java.util.Optional;

/**
 * Defines the basic CRUD operations for a repository.
 *
 * @param <K> the type of the entity identifier
 * @param <T> the type of the entity managed by the repository
 */
public interface RepositoryInterface<K, T> {

    /**
     * Retrieves all entities stored in the repository.
     *
     * @return a list containing all stored entities
     */
    List<T> findAll();

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id the identifier of the entity to retrieve
     * @return an {@code Optional} containing the entity if found, or empty otherwise
     */
    Optional<T> findById(K id);

    /**
     * Saves the specified entity in the repository.
     *
     * @param value the entity to save
     * @return the saved entity
     * @throws DuplicateEmailException if the entity cannot be saved
     */
    T save(T value) throws DuplicateEmailException;

    /**
     * Deletes the entity with the specified identifier.
     *
     * @param id the identifier of the entity to delete
     */
    void deleteById(K id);
}
