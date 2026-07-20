package cloudflight.integra.backend.user.exceptions;

/**
 * Exception thrown when a requested user cannot be found.
 */
public class UserNotFoundException extends Exception {

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message the detail message describing why the user was not found
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
