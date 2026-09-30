import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, delay, of, throwError } from 'rxjs';

import { Booking, BookingApi, BookingPerson, ItemChoice, StockItem } from './booking-api';

const LATENCY_MS = 250;

const USERS: readonly BookingPerson[] = [
  { userId: 4, name: 'Beta Venue', email: 'venueadmin2@example.com' },
  { userId: 5, name: 'Primero Participant', email: 'participant1@example.com' },
  { userId: 6, name: 'Secundo Participant', email: 'participant2@example.com' },
  { userId: 7, name: 'Tercero Participant', email: 'participant3@example.com' },
  { userId: 8, name: 'Cuatro Participant', email: 'participant4@example.com' },
  { userId: 9, name: 'Cinco Participant', email: 'participant5@example.com' },
  { userId: 10, name: 'Seis Participant', email: 'participant6@example.com' },
  { userId: 11, name: 'Siete Participant', email: 'participant7@example.com' },
  { userId: 12, name: 'Ocho Participant', email: 'participant8@example.com' },
];

const ORGANIZER = USERS[0];

const people = (...ids: number[]) => USERS.filter((user) => ids.includes(user.userId));

const STOCK: Record<number, readonly StockItem[]> = {
  1: [
    { id: 1, name: 'Basketballs', totalQuantity: 25, availableQuantity: 18 },
    { id: 2, name: 'Training Cones Set', totalQuantity: 40, availableQuantity: 40 },
    { id: 3, name: 'Soccer Balls', totalQuantity: 30, availableQuantity: 22 },
    { id: 4, name: 'Squash Rackets', totalQuantity: 12, availableQuantity: 8 },
    { id: 5, name: 'Catan & Carcassonne Sets', totalQuantity: 6, availableQuantity: 6 },
  ],
  2: [
    { id: 6, name: 'Projector & Screen Kit', totalQuantity: 5, availableQuantity: 3 },
    { id: 7, name: 'Folding Chairs', totalQuantity: 120, availableQuantity: 95 },
    { id: 8, name: 'Volleyballs', totalQuantity: 15, availableQuantity: 12 },
    { id: 9, name: 'Table Tennis Paddles & Balls Set', totalQuantity: 10, availableQuantity: 7 },
    { id: 10, name: 'Extension Cords & Power Strips', totalQuantity: 20, availableQuantity: 16 },
  ],
};

const CLUJ_ARENA = {
  venueId: 1,
  venueName: 'Cluj Arena',
  venueAddress: 'Aleea Stadionului 2, 400375 Cluj-Napoca',
  venueDescription: 'Premier multi-sport complex and training center',
};

const LAKESIDE = {
  venueId: 2,
  venueName: 'Lakeside Community Center',
  venueAddress: 'Aleea Targului 5, 500220 Piatra-Neamt',
  venueDescription: 'Modern hall and outdoor facilities for social events',
};

function hourStartingAt(daysFromNow: number, hour: number): Pick<Booking, 'start' | 'end'> {
  const start = new Date();
  start.setDate(start.getDate() + daysFromNow);
  start.setHours(hour, 0, 0, 0);

  return {
    start: start.toISOString(),
    end: new Date(start.getTime() + 60 * 60 * 1000).toISOString(),
  };
}

function seed(): Booking[] {
  const base = { status: 'ACTIVE' as const, organizer: ORGANIZER, requests: [] };

  return [
    {
      ...base,
      ...CLUJ_ARENA,
      ...hourStartingAt(2, 10),
      id: 1,
      resourceId: 1,
      resourceName: 'Main Basketball Court',
      item: { inventoryId: 1, name: 'Basketballs', quantity: 4 },
      participants: people(5, 6, 7, 8),
      requests: people(9, 10, 11),
    },
    {
      ...base,
      ...CLUJ_ARENA,
      ...hourStartingAt(3, 18),
      id: 2,
      resourceId: 2,
      resourceName: 'Outdoor Soccer Pitch',
      item: { inventoryId: 3, name: 'Soccer Balls', quantity: 2 },
      participants: people(5, 9, 10, 11, 12),
    },
    {
      ...base,
      ...CLUJ_ARENA,
      ...hourStartingAt(5, 9),
      id: 3,
      resourceId: 3,
      resourceName: 'Squash Court A',
      item: null,
      participants: people(6),
      requests: people(7),
    },
    {
      ...base,
      ...LAKESIDE,
      ...hourStartingAt(2, 16),
      id: 4,
      resourceId: 6,
      resourceName: 'Lakeside Volleyball Court',
      item: { inventoryId: 8, name: 'Volleyballs', quantity: 3 },
      participants: people(7, 8, 9),
    },
    {
      ...base,
      ...LAKESIDE,
      ...hourStartingAt(6, 12),
      id: 5,
      resourceId: 5,
      resourceName: 'Oak Conference Room',
      item: { inventoryId: 6, name: 'Projector & Screen Kit', quantity: 1 },
      participants: people(10, 11),
    },
    {
      ...base,
      ...LAKESIDE,
      ...hourStartingAt(-3, 17),
      id: 6,
      resourceId: 7,
      resourceName: 'Table Tennis & Billiards Room',
      item: null,
      participants: people(12),
    },
  ];
}

function fail(status: number, message: string): Observable<never> {
  return throwError(() => new HttpErrorResponse({ status, error: { messages: [message] } }));
}

/** Serves the signed-in organiser's reservations from memory until there is a backend to ask. */
@Injectable()
export class MockBookingApi extends BookingApi {
  private readonly bookings = new Map(seed().map((booking) => [booking.id, booking]));
  private readonly stock = new Map(
    Object.entries(STOCK).map(([venueId, items]) => [
      Number(venueId),
      items.map((item) => ({ ...item })),
    ]),
  );

  listMine(term?: string): Observable<Booking[]> {
    const now = Date.now();
    const needle = term?.trim().toLowerCase() ?? '';

    const found = [...this.bookings.values()]
      .filter((booking) => booking.organizer.userId === ORGANIZER.userId)
      .filter((booking) => booking.status === 'ACTIVE' && Date.parse(booking.end) > now)
      .filter(
        (booking) =>
          !needle ||
          booking.resourceName.toLowerCase().includes(needle) ||
          booking.venueName.toLowerCase().includes(needle),
      )
      .sort((a, b) => a.start.localeCompare(b.start));

    return of(found).pipe(delay(LATENCY_MS));
  }

  get(id: number): Observable<Booking> {
    const booking = this.bookings.get(id);

    return booking ? of(booking).pipe(delay(LATENCY_MS)) : fail(404, 'Reservation not found.');
  }

  cancel(id: number): Observable<Booking> {
    const booking = this.bookings.get(id);

    if (booking?.status === 'CANCELLED') {
      return fail(400, 'This reservation is already cancelled.');
    }

    return this.change(id, () => ({ status: 'CANCELLED' }));
  }

  listStock(venueId: number): Observable<StockItem[]> {
    const items = (this.stock.get(venueId) ?? []).map((item) => ({ ...item }));

    return of(items).pipe(delay(LATENCY_MS));
  }

  setItem(id: number, choice: ItemChoice | null): Observable<Booking> {
    const booking = this.bookings.get(id);
    if (!booking) {
      return fail(404, 'Reservation not found.');
    }

    const items = this.stock.get(booking.venueId) ?? [];
    const quantity = choice?.quantity ?? 0;
    const wanted = quantity ? items.find((item) => item.id === choice?.inventoryId) : undefined;
    if (quantity && !wanted) {
      return fail(400, 'That item does not belong to this venue.');
    }

    const previous = items.find((item) => item.id === booking.item?.inventoryId);
    const held = booking.item?.quantity ?? 0;
    const free = wanted ? wanted.availableQuantity + (wanted === previous ? held : 0) : 0;
    if (wanted && quantity > free) {
      return fail(400, `Only ${free} ${wanted.name} are free.`);
    }

    if (previous) {
      previous.availableQuantity += held;
    }

    if (!wanted) {
      return this.change(id, () => ({ item: null }));
    }

    wanted.availableQuantity -= quantity;

    return this.change(id, () => ({
      item: { inventoryId: wanted.id, name: wanted.name, quantity },
    }));
  }

  addParticipant(id: number, email: string): Observable<Booking> {
    const booking = this.bookings.get(id);
    const user = USERS.find((candidate) => candidate.email === email.trim().toLowerCase());

    if (!user) {
      return fail(404, `No user is registered as ${email.trim()}.`);
    }

    if (
      booking &&
      (booking.organizer.userId === user.userId ||
        booking.participants.some((person) => person.userId === user.userId))
    ) {
      return fail(400, `${user.name} is already taking part.`);
    }

    return this.change(id, (current) => ({
      participants: [...current.participants, user],
      requests: current.requests.filter((person) => person.userId !== user.userId),
    }));
  }

  removeParticipant(id: number, userId: number): Observable<Booking> {
    return this.change(id, (current) => ({
      participants: current.participants.filter((person) => person.userId !== userId),
    }));
  }

  approveRequests(id: number, userIds: readonly number[]): Observable<Booking> {
    return this.change(id, (current) => ({
      participants: [
        ...current.participants,
        ...current.requests.filter((person) => userIds.includes(person.userId)),
      ],
      requests: current.requests.filter((person) => !userIds.includes(person.userId)),
    }));
  }

  declineRequest(id: number, userId: number): Observable<Booking> {
    return this.change(id, (current) => ({
      requests: current.requests.filter((person) => person.userId !== userId),
    }));
  }

  private change(id: number, patch: (current: Booking) => Partial<Booking>): Observable<Booking> {
    const current = this.bookings.get(id);
    if (!current) {
      return fail(404, 'Reservation not found.');
    }

    const next = { ...current, ...patch(current) };
    this.bookings.set(id, next);

    return of(next).pipe(delay(LATENCY_MS));
  }
}
