import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { delay } from 'rxjs/operators';

import {
  BookedSlot,
  CreatedReservation,
  InventoryOption,
  ReservationApi,
  ReservationDraft,
  ResourceOption,
  UserOption,
  VenueOption,
  overlaps,
} from './reservation-models';

const LATENCY_MS = 250;

let nextReservationId = 1;

export const MOCK_VENUES: VenueOption[] = [
  { id: 1, name: 'Cluj Arena', address: 'Aleea Stadionului 2, 400375 Cluj-Napoca' },
  { id: 2, name: 'Lakeside Community Center', address: 'Aleea Targului 5, 500220 Piatra-Neamt' },
];

export const MOCK_RESOURCES: ResourceOption[] = [
  {
    id: 1,
    venueId: 1,
    name: 'Main Basketball Court',
    activityType: 'Basketball',
    activityDescription: 'Hardwood indoor full-sized court',
    type: 'INDOOR_SPORT',
    capacity: 30,
    hourlyRate: 55,
  },
  {
    id: 2,
    venueId: 1,
    name: 'Outdoor Soccer Pitch',
    activityType: 'Soccer',
    activityDescription: 'Regulation turf soccer pitch with lighting',
    type: 'OUTDOOR_SPORT',
    capacity: 50,
    hourlyRate: 80,
  },
  {
    id: 3,
    venueId: 1,
    name: 'Squash Court A',
    activityType: 'Squash',
    activityDescription: 'Glass-back regulation squash court',
    type: 'INDOOR_SPORT',
    capacity: 4,
    hourlyRate: 40,
  },
  {
    id: 4,
    venueId: 1,
    name: 'VIP Lounge & Games',
    activityType: 'Social Gathering',
    activityDescription: 'Equipped with board games and lounge seating',
    type: 'BOARDGAME_SOCIAL',
    capacity: 25,
    hourlyRate: 45,
  },
  {
    id: 5,
    venueId: 2,
    name: 'Oak Conference Room',
    activityType: 'Workshops',
    activityDescription: 'Equipped with presentation monitors and seating',
    type: 'BOARDGAME_SOCIAL',
    capacity: 20,
    hourlyRate: 35,
  },
  {
    id: 6,
    venueId: 2,
    name: 'Lakeside Volleyball Court',
    activityType: 'Beach Volleyball',
    activityDescription: 'Sand court near the lakefront',
    type: 'OUTDOOR_SPORT',
    capacity: 16,
    hourlyRate: 30,
  },
  {
    id: 7,
    venueId: 2,
    name: 'Table Tennis & Billiards Room',
    activityType: 'Table Games',
    activityDescription: 'Recreational parlor with ping pong and pool tables',
    type: 'INDOOR_SPORT',
    capacity: 15,
    hourlyRate: 25,
  },
];

export const MOCK_INVENTORY: InventoryOption[] = [
  { id: 1, venueId: 1, name: 'Basketballs', totalQuantity: 25, availableQuantity: 18 },
  { id: 2, venueId: 1, name: 'Training Cones Set', totalQuantity: 40, availableQuantity: 40 },
  { id: 3, venueId: 1, name: 'Soccer Balls', totalQuantity: 30, availableQuantity: 22 },
  { id: 4, venueId: 1, name: 'Squash Rackets', totalQuantity: 12, availableQuantity: 8 },
  { id: 5, venueId: 1, name: 'Catan & Carcassonne Sets', totalQuantity: 6, availableQuantity: 6 },
  { id: 6, venueId: 2, name: 'Projector & Screen Kit', totalQuantity: 5, availableQuantity: 3 },
  { id: 7, venueId: 2, name: 'Folding Chairs', totalQuantity: 120, availableQuantity: 95 },
  { id: 8, venueId: 2, name: 'Volleyballs', totalQuantity: 15, availableQuantity: 12 },
  {
    id: 9,
    venueId: 2,
    name: 'Table Tennis Paddles & Balls Set',
    totalQuantity: 10,
    availableQuantity: 7,
  },
  {
    id: 10,
    venueId: 2,
    name: 'Extension Cords & Power Strips',
    totalQuantity: 20,
    availableQuantity: 16,
  },
];

export const MOCK_USERS: UserOption[] = [
  { id: 1, name: 'John Admin', email: 'admin1@example.com' },
  { id: 2, name: 'Jane Admin', email: 'admin2@example.com' },
  { id: 3, name: 'Alpha Venue', email: 'venueadmin1@example.com' },
  { id: 4, name: 'Beta Venue', email: 'venueadmin2@example.com' },
  { id: 5, name: 'Primero Participant', email: 'participant1@example.com' },
  { id: 6, name: 'Secundo Participant', email: 'participant2@example.com' },
  { id: 7, name: 'Tercero Participant', email: 'participant3@example.com' },
  { id: 8, name: 'Cuatro Participant', email: 'participant4@example.com' },
  { id: 9, name: 'Cinco Participant', email: 'participant5@example.com' },
  { id: 10, name: 'Seis Participant', email: 'participant6@example.com' },
  { id: 11, name: 'Siete Participant', email: 'participant7@example.com' },
  { id: 12, name: 'Ocho Participant', email: 'participant8@example.com' },
];

export const MOCK_ORGANISER: UserOption = MOCK_USERS[2];

function slot(dayOffset: number, startHour: number, endHour: number): { start: string; end: string } {
  const start = new Date();
  start.setDate(start.getDate() + dayOffset);
  start.setHours(startHour, 0, 0, 0);

  const end = new Date(start);
  end.setHours(endHour, 0, 0, 0);

  return { start: start.toISOString(), end: end.toISOString() };
}

export const MOCK_BOOKED_SLOTS: BookedSlot[] = [
  { resourceId: 1, ...slot(0, 10, 11) },
  { resourceId: 1, ...slot(0, 18, 19) },
  { resourceId: 1, ...slot(1, 9, 10) },
  { resourceId: 2, ...slot(0, 9, 11) },
  { resourceId: 3, ...slot(1, 17, 18) },
  { resourceId: 5, ...slot(1, 14, 15) },
  { resourceId: 6, ...slot(0, 12, 13) },
];

@Injectable()
export class MockReservationApi extends ReservationApi {
  private readonly created: BookedSlot[] = [];

  listVenues(): Observable<VenueOption[]> {
    return of(MOCK_VENUES).pipe(delay(LATENCY_MS));
  }

  listResources(venueId: number): Observable<ResourceOption[]> {
    return of(MOCK_RESOURCES.filter((resource) => resource.venueId === venueId)).pipe(
      delay(LATENCY_MS),
    );
  }

  listInventory(venueId: number): Observable<InventoryOption[]> {
    return of(MOCK_INVENTORY.filter((item) => item.venueId === venueId)).pipe(delay(LATENCY_MS));
  }

  listBookedSlots(resourceId: number): Observable<BookedSlot[]> {
    return of(this.bookingsFor(resourceId)).pipe(delay(LATENCY_MS));
  }

  listUsers(term: string): Observable<UserOption[]> {
    const needle = term.trim().toLowerCase();

    if (!needle) {
      return of(MOCK_USERS).pipe(delay(LATENCY_MS));
    }

    const matches = MOCK_USERS.filter(
      (user) =>
        user.name.toLowerCase().includes(needle) || user.email.toLowerCase().includes(needle),
    );

    return of(matches).pipe(delay(LATENCY_MS));
  }

  organiser(): Observable<UserOption> {
    return of(MOCK_ORGANISER).pipe(delay(LATENCY_MS));
  }

  createReservation(draft: ReservationDraft): Observable<CreatedReservation> {
    const conflict = this.bookingsFor(draft.resourceId).find((booked) =>
      overlaps(draft.start, draft.end, booked.start, booked.end),
    );

    if (conflict) {
      return throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
            statusText: 'Conflict',
            error: {
              messages: [
                `The resource is already booked from ${formatTime(conflict.start)} to ${formatTime(conflict.end)}.`,
              ],
            },
          }),
      ).pipe(delay(LATENCY_MS));
    }

    this.created.push({ resourceId: draft.resourceId, start: draft.start, end: draft.end });

    return of({
      id: nextReservationId++,
      start: draft.start,
      end: draft.end,
      status: 'PENDING' as const,
    }).pipe(delay(LATENCY_MS));
  }

  private bookingsFor(resourceId: number): BookedSlot[] {
    return [...MOCK_BOOKED_SLOTS, ...this.created].filter(
      (booked) => booked.resourceId === resourceId,
    );
  }
}

function formatTime(instant: string): string {
  return new Date(instant).toLocaleString([], {
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  });
}
