package cloudflight.integra.backend.user.exceptions;

/**
 * Base exception for errors related to user repository operations.
 */
public class RepositoryException extends Exception {

    /**
     * Creates a new repository exception with the specified detail message.
     *
     * @param message the detail message describing the repository error
     */
    public RepositoryException(String message) {
        super(message);
    }
}
