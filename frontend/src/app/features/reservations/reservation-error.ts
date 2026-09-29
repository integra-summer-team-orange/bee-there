import { HttpErrorResponse } from '@angular/common/http';

interface BackendError {
  messages?: string[];
}

const SLOT_TAKEN = 'That time is already taken for this resource. Pick another slot or another resource.';

export function describeReservationError(error: HttpErrorResponse): string {
  const messages = (error.error as BackendError | null)?.messages;

  if (error.status === 400 && messages?.some((message) => message.includes('already a reservation'))) {
    return SLOT_TAKEN;
  }

  if (error.status === 409) {
    const clash = messages?.join(' ');

    return clash ? `That time is already taken. ${clash}` : SLOT_TAKEN;
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

  return messages?.length ? messages.join(' ') : 'Something went wrong. Please try again.';
}
