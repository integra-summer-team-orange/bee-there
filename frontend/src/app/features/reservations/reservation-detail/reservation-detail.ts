import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MessageService } from 'primeng/api';
import { AvatarModule } from 'primeng/avatar';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';
import { Observable } from 'rxjs';

import { Booking, BookingApi, BookingPerson } from '../booking-api';
import { describeBookingError } from '../booking-error';
import { Notice, showNavigationNotice } from '../booking-notice';

type PeopleTab = 'participants' | 'requests';

const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/** One reservation: what was booked, who is taking part and who is asking to. */
@Component({
  selector: 'app-reservation-detail',
  imports: [
    DatePipe,
    FormsModule,
    RouterLink,
    AvatarModule,
    ButtonModule,
    CardModule,
    DialogModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    MessageModule,
    ProgressSpinnerModule,
    TagModule,
    ToastModule,
    TooltipModule,
  ],
  templateUrl: './reservation-detail.html',
  styleUrl: './reservation-detail.css',
})
export class ReservationDetail {
  private readonly api = inject(BookingApi);
  private readonly messages = inject(MessageService);
  private readonly router = inject(Router);

  protected readonly id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));

  protected readonly booking = signal<Booking | null>(null);
  protected readonly loading = signal(true);
  protected readonly working = signal(false);
  protected readonly tab = signal<PeopleTab>('participants');
  protected readonly search = signal('');

  protected readonly addingUser = signal(false);
  protected readonly newEmail = signal('');
  protected readonly addError = signal('');

  protected readonly cancelAsked = signal(false);

  protected readonly avatarStyle = {
    background: 'var(--color-primary-fixed)',
    color: 'var(--color-general-highlight)',
  };

  protected readonly cancelled = computed(() => this.booking()?.status === 'CANCELLED');

  protected readonly people = computed(() => {
    const booking = this.booking();
    if (!booking) {
      return [];
    }

    const list = this.tab() === 'participants' ? booking.participants : booking.requests;
    const needle = this.search().trim().toLowerCase();

    return list.filter(
      (person) =>
        !needle ||
        person.name.toLowerCase().includes(needle) ||
        person.email.toLowerCase().includes(needle),
    );
  });

  protected readonly organizerShown = computed(() => {
    const organizer = this.booking()?.organizer;
    const needle = this.search().trim().toLowerCase();

    return (
      !!organizer &&
      this.tab() === 'participants' &&
      (!needle ||
        organizer.name.toLowerCase().includes(needle) ||
        organizer.email.toLowerCase().includes(needle))
    );
  });

  protected readonly emailValid = computed(() => EMAIL.test(this.newEmail().trim()));

  constructor() {
    showNavigationNotice();

    this.api.get(this.id).subscribe({
      next: (booking) => {
        this.booking.set(booking);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messages.add({
          severity: 'error',
          summary: 'Could not load reservation',
          detail: describeBookingError(error),
        });
      },
    });
  }

  protected openAddUser(): void {
    this.newEmail.set('');
    this.addError.set('');
    this.addingUser.set(true);
  }

  protected addUser(): void {
    if (!this.emailValid() || this.working()) {
      return;
    }

    const email = this.newEmail().trim();
    this.working.set(true);

    this.api.addParticipant(this.id, email).subscribe({
      next: (booking) => {
        this.working.set(false);
        this.booking.set(booking);
        this.addingUser.set(false);
        this.tab.set('participants');
        this.messages.add({ severity: 'success', summary: 'Participant added', detail: email });
      },
      error: (error: HttpErrorResponse) => {
        this.working.set(false);
        this.addError.set(describeBookingError(error));
      },
    });
  }

  protected remove(person: BookingPerson): void {
    const call =
      this.tab() === 'participants'
        ? this.api.removeParticipant(this.id, person.userId)
        : this.api.declineRequest(this.id, person.userId);

    this.run(call, 'Could not remove ' + person.name);
  }

  protected approve(people: readonly BookingPerson[]): void {
    if (people.length === 0) {
      return;
    }

    this.run(
      this.api.approveRequests(
        this.id,
        people.map((person) => person.userId),
      ),
      'Could not approve the request',
    );
  }

  protected confirmCancel(): void {
    if (this.working()) {
      return;
    }

    this.working.set(true);

    this.api.cancel(this.id).subscribe({
      next: (booking) => {
        this.working.set(false);
        this.cancelAsked.set(false);
        const notice: Notice = {
          summary: 'Reservation cancelled',
          detail: `${booking.resourceName} is no longer booked.`,
        };
        this.router.navigate(['/reservations'], { state: { notice } });
      },
      error: (error: HttpErrorResponse) => {
        this.working.set(false);
        this.cancelAsked.set(false);
        this.messages.add({
          severity: 'error',
          summary: 'Could not cancel reservation',
          detail: describeBookingError(error),
        });
      },
    });
  }

  private run(call: Observable<Booking>, failure: string): void {
    if (this.working()) {
      return;
    }

    this.working.set(true);

    call.subscribe({
      next: (booking) => {
        this.working.set(false);
        this.booking.set(booking);
      },
      error: (error: HttpErrorResponse) => {
        this.working.set(false);
        this.messages.add({
          severity: 'error',
          summary: failure,
          detail: describeBookingError(error),
        });
      },
    });
  }
}
