package cloudflight.integra.backend.user.exceptions;

/**
 * Exception thrown when attempting to create or update a user
 * with an email address that already exists.
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * Creates a new exception with the specified error message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public DuplicateEmailException(String message) {
        super(message);
    }
}
