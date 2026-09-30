import { formatDate } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map, of, throwError } from 'rxjs';

import {
  InventoryDto,
  ReservationsService,
  ResourceDto,
  UserResponseDto,
  UsersService,
  VenuesService,
} from '../../../api/generated';
import { Session } from '../../core/services/session';
import {
  BookedSlot,
  CreatedReservation,
  InventoryOption,
  ReservationApi,
  ReservationDraft,
  ResourceOption,
  UserOption,
  VenueOption,
} from './reservation-models';

const PAGE_SIZE = 100;

@Injectable()
export class HttpReservationApi extends ReservationApi {
  private readonly venues = inject(VenuesService);
  private readonly users = inject(UsersService);
  private readonly reservations = inject(ReservationsService);
  private readonly session = inject(Session);

  listVenues(): Observable<VenueOption[]> {
    return this.venues
      .getAllVenues(0, PAGE_SIZE)
      .pipe(
        map((page) =>
          (page.content ?? []).map((venue) => ({
            id: venue.id!,
            name: venue.name,
            address: venue.address,
          })),
        ),
      );
  }

  listResources(venueId: number): Observable<ResourceOption[]> {
    return this.venues.getResourcesByVenue(venueId, 0, PAGE_SIZE).pipe(
      map((page) =>
        ((page.content ?? []) as ResourceDto[]).map((resource) => ({
          id: resource.id!,
          venueId: resource.venueId,
          name: resource.name,
          activityType: resource.activityType,
          activityDescription: resource.activityDescription,
          type: resource.type ?? '',
          capacity: resource.capacity,
          hourlyRate: resource.hourlyRate,
        })),
      ),
    );
  }

  listInventory(venueId: number): Observable<InventoryOption[]> {
    return this.venues.getInventoryByVenue(venueId, 0, PAGE_SIZE).pipe(
      map((page) =>
        ((page.content ?? []) as InventoryDto[]).map((item) => ({
          id: item.id!,
          venueId: item.venueId,
          name: item.name,
          totalQuantity: item.totalQuantity,
          availableQuantity: item.availableQuantity,
        })),
      ),
    );
  }

  listBookedSlots(_resourceId: number): Observable<BookedSlot[]> {
    return of([]);
  }

  listUsers(term: string): Observable<UserOption[]> {
    const needle = term.trim().toLowerCase();

    return this.users.getAllUsers(0, PAGE_SIZE).pipe(
      map((page) =>
        (page.content ?? [])
          .map(toUserOption)
          .filter(
            (user) =>
              !needle ||
              user.name.toLowerCase().includes(needle) ||
              user.email.toLowerCase().includes(needle),
          ),
      ),
    );
  }

  organiser(): Observable<UserOption> {
    const userId = this.session.userId();

    if (userId === null) {
      return throwError(() => new HttpErrorResponse({ status: 401, statusText: 'Unauthorized' }));
    }

    return this.users.getUserById(userId).pipe(map(toUserOption));
  }

  createReservation(draft: ReservationDraft): Observable<CreatedReservation> {
    return this.reservations
      .createReservation({
        venueId: draft.venueId,
        resourceId: draft.resourceId,
        inventoryId: draft.inventoryId,
        startTime: toLocalDateTime(draft.start),
        endTime: toLocalDateTime(draft.end),
        maxParticipants: draft.maxParticipants,
      })
      .pipe(
        map((reservation) => ({
          id: reservation.id!,
          start: reservation.startTime ?? draft.start,
          end: reservation.endTime ?? draft.end,
          status: reservation.status ?? 'ACTIVE',
        })),
      );
  }
}

function toUserOption(user: UserResponseDto): UserOption {
  return { id: user.id!, name: user.name ?? '', email: user.email ?? '' };
}

/** The backend binds a zone-less `LocalDateTime`, so an ISO instant would arrive shifted to UTC. */
function toLocalDateTime(instant: string): string {
  return formatDate(instant, "yyyy-MM-dd'T'HH:mm:ss", 'en-US');
}
