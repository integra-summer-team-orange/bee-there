import { HttpErrorResponse } from '@angular/common/http';

interface BackendError {
  messages?: string[];
}

export function describeReservationError(error: HttpErrorResponse): string {
  if (error.status === 409) {
    const clash = (error.error as BackendError | null)?.messages?.join(' ');

    return clash
      ? `That time is already taken. ${clash}`
      : 'That time is already taken for this resource. Pick another slot or another resource.';
  }

  if (error.status === 401) {
    return 'Your session is not valid. Sign in again to continue.';
  }

  if (error.status === 403) {
    return 'You are not allowed to book this resource.';
  }

  if (error.status === 0) {
    return 'The server could not be reached.';
  }

  const messages = (error.error as BackendError | null)?.messages;

  return messages?.length ? messages.join(' ') : 'Something went wrong. Please try again.';
}
