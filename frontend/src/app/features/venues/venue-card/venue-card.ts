import { Component, input, output } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';

import { VenueDto } from '../../../../api/generated';

/** A single venue in the overview grid, with the actions the current user is allowed to take on it. */
@Component({
  selector: 'app-venue-card',
  imports: [DatePipe, RouterLink, ButtonModule, CardModule],
  templateUrl: './venue-card.html',
  styleUrl: './venue-card.css',
})
export class VenueCard {
  /** The venue to render. */
  readonly venue = input.required<VenueDto>();

  /** Whether the current user may edit or delete this venue. */
  readonly manageable = input<boolean>(false);

  /** Emitted when the user asks to delete this venue. */
  readonly remove = output<VenueDto>();
}
