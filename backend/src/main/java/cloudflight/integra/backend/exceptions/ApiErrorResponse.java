package cloudflight.integra.backend.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
}
