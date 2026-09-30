import { Component, computed, inject, input, output, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Subject, debounceTime, distinctUntilChanged, startWith, switchMap } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { TabsModule } from 'primeng/tabs';
import { TagModule } from 'primeng/tag';

import { Participant, ReservationApi, UserOption, isPlausibleEmail } from '../reservation-models';

const SEARCH_DEBOUNCE_MS = 300;

@Component({
  selector: 'app-participant-picker',
  imports: [
    FormsModule,
    ButtonModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    TabsModule,
    TagModule,
  ],
  templateUrl: './participant-picker.html',
  styleUrl: './participant-picker.css',
})
export class ParticipantPicker {
  private readonly api = inject(ReservationApi);

  readonly participants = input.required<readonly Participant[]>();

  readonly add = output<Participant>();

  readonly remove = output<Participant>();

  protected readonly tab = signal<'people' | 'invitations'>('people');
  protected readonly term = signal('');
  protected readonly loading = signal(true);
  protected readonly users = signal<UserOption[]>([]);
  protected readonly email = signal('');
  protected readonly emailError = signal<string | null>(null);

  protected readonly available = computed(() =>
    this.users().filter((user) => !this.alreadyAdded(user.email)),
  );

  private readonly terms = new Subject<string>();

  constructor() {
    this.terms
      .pipe(
        debounceTime(SEARCH_DEBOUNCE_MS),
        distinctUntilChanged(),
        startWith(''),
        switchMap((term) => this.api.listUsers(term)),
        takeUntilDestroyed(),
      )
      .subscribe({
        next: (users) => {
          this.users.set(users);
          this.loading.set(false);
        },
        error: () => this.loading.set(false),
      });
  }

  protected onTermChange(value: string): void {
    this.term.set(value);
    this.terms.next(value);
  }

  protected addUser(user: UserOption): void {
    if (this.alreadyAdded(user.email)) {
      return;
    }

    this.add.emit({
      kind: 'user',
      userId: user.id,
      name: user.name,
      email: user.email,
      organiser: false,
    });
  }

  protected invite(): void {
    const address = this.email().trim();

    if (!isPlausibleEmail(address)) {
      this.emailError.set('Enter a valid email address.');
      return;
    }

    if (this.alreadyAdded(address)) {
      this.emailError.set('That person is already on the list.');
      return;
    }

    this.add.emit({ kind: 'email', email: address });
    this.email.set('');
    this.emailError.set(null);
  }

  protected onEmailChange(value: string): void {
    this.email.set(value);
    this.emailError.set(null);
  }

  protected label(participant: Participant): string {
    return participant.kind === 'user' ? participant.name : participant.email;
  }

  protected isOrganiser(participant: Participant): boolean {
    return participant.kind === 'user' && participant.organiser;
  }

  private alreadyAdded(email: string): boolean {
    const needle = email.trim().toLowerCase();

    return this.participants().some((participant) => participant.email.toLowerCase() === needle);
  }
}
