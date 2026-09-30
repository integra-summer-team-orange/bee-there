import { Component, computed, input } from '@angular/core';
import { AvatarModule } from 'primeng/avatar';
import { AvatarGroupModule } from 'primeng/avatargroup';

import { BookingPerson } from '../booking-api';

const SHOWN = 3;

/** A few avatars for the people taking part, then a count of the rest. */
@Component({
  selector: 'app-participant-stack',
  imports: [AvatarModule, AvatarGroupModule],
  templateUrl: './participant-stack.html',
  styleUrl: './participant-stack.css',
})
export class ParticipantStack {
  readonly people = input.required<readonly BookingPerson[]>();

  protected readonly avatarStyle = {
    background: 'var(--color-primary-fixed)',
    color: 'var(--color-general-highlight)',
  };

  protected readonly shown = computed(() => this.people().slice(0, SHOWN));
  protected readonly hidden = computed(() => Math.max(this.people().length - SHOWN, 0));
  protected readonly names = computed(() => this.people().map((person) => person.name).join(', '));
}
