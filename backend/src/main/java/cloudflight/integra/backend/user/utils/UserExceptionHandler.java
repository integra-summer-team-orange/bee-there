package cloudflight.integra.backend.user.utils;


import cloudflight.integra.backend.user.exceptions.DuplicateEmailException;
import cloudflight.integra.backend.user.exceptions.UserNotFoundException;
import cloudflight.integra.backend.user.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Handles application exceptions globally and converts them into
 * consistent HTTP error responses.
 */
@RestControllerAdvice
public class UserExceptionHandler {

    /**
     * Handles exceptions thrown when a duplicate email address is detected.
     *
     * @param ex the exception that was thrown
     * @param request the HTTP request that caused the exception
     * @return a response containing the error details with HTTP status 400
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
        DuplicateEmailException ex,
        HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            List.of(ex.getMessage()),
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles exceptions thrown when a requested user cannot be found.
     *
     * @param ex the exception that was thrown
     * @param request the HTTP request that caused the exception
     * @return a response containing the error details with HTTP status 404
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
        UserNotFoundException ex,
        HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            List.of(ex.getMessage()),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles bean validation failures for request DTOs.
     *
     * @param ex the validation exception containing all validation errors
     * @param request the HTTP request that caused the validation failure
     * @return a response containing the validation error messages with HTTP status 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {

        List<String> messages = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList();

        ErrorResponse response = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            messages,
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(response);
    }
}
