import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';

import { Booking, BookingApi } from '../booking-api';
import { describeBookingError } from '../booking-error';
import { showNavigationNotice } from '../booking-notice';
import { ParticipantStack } from '../participant-stack/participant-stack';
import { ReservationCard } from '../reservation-card/reservation-card';

const SEARCH_DEBOUNCE_MS = 300;

/** The organiser's upcoming reservations, grouped by venue, with the chosen one shown beside them. */
@Component({
  selector: 'app-reservation-overview',
  imports: [
    DatePipe,
    FormsModule,
    RouterLink,
    ButtonModule,
    CardModule,
    DialogModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    ProgressSpinnerModule,
    ToastModule,
    TooltipModule,
    ParticipantStack,
    ReservationCard,
  ],
  templateUrl: './reservation-overview.html',
  styleUrl: './reservation-overview.css',
})
export class ReservationOverview {
  private readonly api = inject(BookingApi);
  private readonly messages = inject(MessageService);

  protected readonly loading = signal(false);
  protected readonly cancelling = signal(false);
  protected readonly search = signal('');
  protected readonly bookings = signal<Booking[]>([]);
  protected readonly selectedId = signal<number | null>(null);
  protected readonly toCancel = signal<Booking | null>(null);

  protected readonly selected = computed<Booking | null>(
    () => this.bookings().find((booking) => booking.id === this.selectedId()) ?? this.bookings()[0] ?? null,
  );

  protected readonly byVenue = computed(() => {
    const groups = new Map<string, Booking[]>();
    for (const booking of this.bookings()) {
      groups.set(booking.venueName, [...(groups.get(booking.venueName) ?? []), booking]);
    }

    return [...groups].map(([venue, bookings]) => ({ venue, bookings }));
  });

  private searchTimer?: ReturnType<typeof setTimeout>;

  constructor() {
    showNavigationNotice();
    this.load();
  }

  protected load(): void {
    this.loading.set(true);

    this.api.listMine(this.search().trim() || undefined).subscribe({
      next: (bookings) => {
        this.bookings.set(bookings);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messages.add({
          severity: 'error',
          summary: 'Could not load reservations',
          detail: describeBookingError(error),
        });
      },
    });
  }

  protected onSearchChange(term: string): void {
    this.search.set(term);
    clearTimeout(this.searchTimer);
    this.searchTimer = setTimeout(() => this.load(), SEARCH_DEBOUNCE_MS);
  }

  protected everyone(booking: Booking) {
    return [booking.organizer, ...booking.participants];
  }

  protected confirmCancel(): void {
    const booking = this.toCancel();
    if (!booking || this.cancelling()) {
      return;
    }

    this.cancelling.set(true);

    this.api.cancel(booking.id).subscribe({
      next: () => {
        this.cancelling.set(false);
        this.toCancel.set(null);
        this.messages.add({
          severity: 'success',
          summary: 'Reservation cancelled',
          detail: `${booking.resourceName} is no longer booked.`,
        });
        this.load();
      },
      error: (error: HttpErrorResponse) => {
        this.cancelling.set(false);
        this.toCancel.set(null);
        this.messages.add({
          severity: 'error',
          summary: 'Could not cancel reservation',
          detail: describeBookingError(error),
        });
      },
    });
  }
}
