import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { MockBookingApi } from './booking-mock';

describe('MockBookingApi', () => {
  let api: MockBookingApi;

  beforeEach(() => {
    api = new MockBookingApi();
  });

  it('lists only upcoming active reservations, soonest first', async () => {
    const bookings = await firstValueFrom(api.listMine());

    expect(bookings.map((booking) => booking.id)).not.toContain(6);
    expect(bookings.every((booking) => booking.status === 'ACTIVE')).toBe(true);
    expect([...bookings].sort((a, b) => a.start.localeCompare(b.start))).toEqual(bookings);
  });

  it('narrows the list by resource or venue name', async () => {
    const byResource = await firstValueFrom(api.listMine('squash'));
    const byVenue = await firstValueFrom(api.listMine('lakeside'));

    expect(byResource.map((booking) => booking.id)).toEqual([3]);
    expect(byVenue.every((booking) => booking.venueId === 2)).toBe(true);
  });

  it('drops a cancelled reservation from the list and refuses to cancel it twice', async () => {
    await firstValueFrom(api.cancel(1));

    const bookings = await firstValueFrom(api.listMine());
    const again = await firstValueFrom(api.cancel(1)).catch((error: HttpErrorResponse) => error);

    expect(bookings.map((booking) => booking.id)).not.toContain(1);
    expect((again as HttpErrorResponse).status).toBe(400);
  });

  it('releases the old item and takes the new one when the item changes', async () => {
    await firstValueFrom(api.setItem(1, { inventoryId: 2, quantity: 5 }));

    const stock = await firstValueFrom(api.listStock(1));
    const booking = await firstValueFrom(api.get(1));

    expect(stock.find((item) => item.id === 1)?.availableQuantity).toBe(22);
    expect(stock.find((item) => item.id === 2)?.availableQuantity).toBe(35);
    expect(booking.item).toEqual({ inventoryId: 2, name: 'Training Cones Set', quantity: 5 });
  });

  it('counts the quantity already held when checking what is free', async () => {
    const booking = await firstValueFrom(api.setItem(1, { inventoryId: 1, quantity: 22 }));
    const tooMany = await firstValueFrom(api.setItem(1, { inventoryId: 1, quantity: 26 })).catch(
      (error: HttpErrorResponse) => error,
    );

    expect(booking.item?.quantity).toBe(22);
    expect((tooMany as HttpErrorResponse).status).toBe(400);
  });

  it('refuses an item from another venue', async () => {
    const error = await firstValueFrom(api.setItem(1, { inventoryId: 8, quantity: 1 })).catch(
      (failure: HttpErrorResponse) => failure,
    );

    expect((error as HttpErrorResponse).status).toBe(400);
  });

  it('adds a registered user by email and refuses unknown or duplicate ones', async () => {
    const booking = await firstValueFrom(api.addParticipant(1, ' Participant8@example.com '));
    const unknown = await firstValueFrom(api.addParticipant(1, 'nobody@example.com')).catch(
      (error: HttpErrorResponse) => error,
    );
    const twice = await firstValueFrom(api.addParticipant(1, 'participant8@example.com')).catch(
      (error: HttpErrorResponse) => error,
    );

    expect(booking.participants.map((person) => person.userId)).toContain(12);
    expect((unknown as HttpErrorResponse).status).toBe(404);
    expect((twice as HttpErrorResponse).status).toBe(400);
  });

  it('moves approved requests into the participants', async () => {
    const booking = await firstValueFrom(api.approveRequests(1, [9, 10]));

    expect(booking.requests.map((person) => person.userId)).toEqual([11]);
    expect(booking.participants.map((person) => person.userId)).toEqual([5, 6, 7, 8, 9, 10]);
  });

  it('answers 404 for a reservation that does not exist', async () => {
    const error = await firstValueFrom(api.get(999)).catch((failure: HttpErrorResponse) => failure);

    expect((error as HttpErrorResponse).status).toBe(404);
  });
});
