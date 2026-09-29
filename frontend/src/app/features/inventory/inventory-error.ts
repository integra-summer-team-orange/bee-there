import { HttpErrorResponse } from '@angular/common/http';

/** The error body the backend's GlobalExceptionHandler returns. */
interface BackendError {
  messages?: string[];
}

/**
 * Turns a failed inventory request into a message worth showing.
 */
export function describeInventoryError(error: HttpErrorResponse): string {
  if (error.status === 401) {
    return 'Your session is not valid. Sign in again to continue.';
  }

  if (error.status === 403) {
    return 'You do not have permission to manage this inventory.';
  }

  if (error.status === 0) {
    return 'The server could not be reached.';
  }

  const messages = (error.error as BackendError | null)?.messages;

  return messages?.length ? messages.join(' ') : 'Something went wrong. Please try again.';
}
