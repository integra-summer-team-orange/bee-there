package cloudflight.integra.backend.user.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the standard error response returned by the API when a request
 * cannot be processed successfully.
 *
 * @param timestamp the date and time when the error occurred
 * @param status the HTTP status code associated with the error
 * @param error the HTTP status reason phrase
 * @param messages the list of error messages describing the failure
 * @param path the request path that caused the error
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    List<String> messages,
    String path
) {}
