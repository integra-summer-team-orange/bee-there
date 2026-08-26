import { HttpErrorResponse } from '@angular/common/http';

/** The error body the backend's GlobalExceptionHandler returns. */
interface BackendError {
  messages?: string[];
}

/**
 * Turns a failed venue request into a message worth showing.
 *
 * A 403 is the ownership check firing server-side, and a 401 means the stored token is missing or expired;
 * both deserve a clearer sentence than the raw body, which is empty for role-based denials.
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
