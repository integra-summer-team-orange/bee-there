import { Component, computed, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';

import { Booking } from '../booking-api';
import { ParticipantStack } from '../participant-stack/participant-stack';

/** One reservation in the overview's list. */
@Component({
  selector: 'app-reservation-card',
  imports: [DatePipe, ParticipantStack],
  templateUrl: './reservation-card.html',
  styleUrl: './reservation-card.css',
})
export class ReservationCard {
  readonly booking = input.required<Booking>();
  readonly selected = input(false);
  readonly choose = output<Booking>();

  protected readonly everyone = computed(() => [
    this.booking().organizer,
    ...this.booking().participants,
  ]);
}
