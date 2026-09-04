import { HttpErrorResponse } from '@angular/common/http';

/** The error body the backend's GlobalExceptionHandler returns. */
interface BackendError {
  messages?: string[];
}

/**
 * Turns a failed venue request into a message worth showing. Role denials come back with an empty body,
 * so 401 and 403 get their own text.
 */
export function describeVenueError(error: HttpErrorResponse): string {
  if (error.status === 401) {
    return 'Your session is not valid. Sign in again to continue.';
  }

  if (error.status === 403) {
    return 'You can only manage venues you own.';
  }

  if (error.status === 0) {
    return 'The server could not be reached.';
  }

  const messages = (error.error as BackendError | null)?.messages;

  return messages?.length ? messages.join(' ') : 'Something went wrong. Please try again.';
}
