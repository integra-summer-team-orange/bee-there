import { HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { MockReservationApi } from './reservation-mock';
import { ReservationDraft, overlaps } from './reservation-models';

function at(dayOffset: number, hour: number): string {
  const date = new Date();
  date.setDate(date.getDate() + dayOffset);
  date.setHours(hour, 0, 0, 0);

  return date.toISOString();
}

function draft(start: string, end: string, resourceId = 4): ReservationDraft {
  return {
    resourceId,
    start,
    end,
    participants: [],
    items: [],
  };
}

describe('overlaps', () => {
  it('treats periods as half-open, so back-to-back bookings do not clash', () => {
    expect(overlaps(at(0, 10), at(0, 12), at(0, 12), at(0, 14))).toBe(false);
  });

  it('catches a period that starts inside another', () => {
    expect(overlaps(at(0, 10), at(0, 12), at(0, 11), at(0, 13))).toBe(true);
  });

  it('catches a period that contains another', () => {
    expect(overlaps(at(0, 9), at(0, 15), at(0, 11), at(0, 12))).toBe(true);
  });
});

describe('MockReservationApi', () => {
  let api: MockReservationApi;

  beforeEach(() => {
    api = new MockReservationApi();
  });

  it('serves only the resources of the requested venue', async () => {
    const resources = await firstValueFrom(api.listResources(2));

    expect(resources.map((resource) => resource.id)).toEqual([5, 6, 7]);
  });

  it('serves only the inventory of the requested venue', async () => {
    const items = await firstValueFrom(api.listInventory(2));

    expect(items.map((item) => item.name)).toContain('Volleyballs');
    expect(items.every((item) => item.venueId === 2)).toBe(true);
  });

  it('mirrors the seeder, so availability is never a flat hundred percent', async () => {
    const items = await firstValueFrom(api.listInventory(1));
    const basketballs = items.find((item) => item.name === 'Basketballs');

    expect(basketballs).toEqual({
      id: 1,
      venueId: 1,
      name: 'Basketballs',
      totalQuantity: 25,
      availableQuantity: 18,
    });
  });

  it('lists everybody when the search term is blank', async () => {
    const users = await firstValueFrom(api.listUsers(''));

    expect(users).toHaveLength(12);
  });

  it('matches users on name and on email', async () => {
    const byName = await firstValueFrom(api.listUsers('primero'));
    const byEmail = await firstValueFrom(api.listUsers('venueadmin2'));

    expect(byName.map((user) => user.id)).toEqual([5]);
    expect(byEmail.map((user) => user.id)).toEqual([4]);
  });

  it('creates a reservation on a free slot', async () => {
    const created = await firstValueFrom(api.createReservation(draft(at(3, 9), at(3, 10))));

    expect(created.status).toBe('PENDING');
    expect(created.id).toBeGreaterThan(0);
  });

  it('refuses an overlapping slot with a 409 the UI can explain', async () => {
    await expect(
      firstValueFrom(api.createReservation(draft(at(0, 10), at(0, 11), 1))),
    ).rejects.toSatisfy(
      (error: HttpErrorResponse) => error.status === 409 && error.error.messages.length === 1,
    );
  });

  it('allows a booking that starts exactly when an existing one ends', async () => {
    const created = await firstValueFrom(api.createReservation(draft(at(0, 11), at(0, 12), 1)));

    expect(created.id).toBeGreaterThan(0);
  });

  it('holds the slot it just handed out against the next booking', async () => {
    await firstValueFrom(api.createReservation(draft(at(4, 9), at(4, 10))));

    await expect(
      firstValueFrom(api.createReservation(draft(at(4, 9), at(4, 10)))),
    ).rejects.toSatisfy((error: HttpErrorResponse) => error.status === 409);
  });
});
