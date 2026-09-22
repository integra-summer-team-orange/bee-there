import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';

import {
  BookedSlot,
  CreatedReservation,
  InventoryOption,
  Participant,
  ReservationApi,
  ResourceOption,
  UserOption,
  VenueOption,
} from '../reservation-models';
import { ParticipantPicker } from './participant-picker';

class ResizeObserverStub {
  observe(): void {}
  unobserve(): void {}
  disconnect(): void {}
}

globalThis.ResizeObserver ??= ResizeObserverStub as unknown as typeof ResizeObserver;

const ORGANISER: Participant = {
  kind: 'user',
  userId: 3,
  name: 'Alpha Venue',
  email: 'venueadmin1@example.com',
  organiser: true,
};

const USERS: UserOption[] = [
  { id: 3, name: 'Alpha Venue', email: 'venueadmin1@example.com' },
  { id: 5, name: 'Primero Participant', email: 'participant1@example.com' },
  { id: 6, name: 'Secundo Participant', email: 'participant2@example.com' },
];

class ReservationApiStub extends ReservationApi {
  listVenues(): Observable<VenueOption[]> {
    return of([]);
  }

  listResources(): Observable<ResourceOption[]> {
    return of([]);
  }

  listInventory(): Observable<InventoryOption[]> {
    return of([]);
  }

  listBookedSlots(): Observable<BookedSlot[]> {
    return of([]);
  }

  listUsers(term: string): Observable<UserOption[]> {
    const needle = term.trim().toLowerCase();

    if (!needle) {
      return of(USERS);
    }

    return of(
      USERS.filter(
        (user) =>
          user.name.toLowerCase().includes(needle) || user.email.toLowerCase().includes(needle),
      ),
    );
  }

  organiser(): Observable<UserOption> {
    return of(USERS[0]);
  }

  createReservation(): Observable<CreatedReservation> {
    return of({ id: 1, start: '', end: '', status: 'PENDING' });
  }
}

describe('ParticipantPicker', () => {
  let fixture: ComponentFixture<ParticipantPicker>;
  let component: ParticipantPicker;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParticipantPicker],
      providers: [{ provide: ReservationApi, useClass: ReservationApiStub }],
    }).compileComponents();

    fixture = TestBed.createComponent(ParticipantPicker);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('participants', [ORGANISER]);
    await fixture.whenStable();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('offers everybody with an account without being searched first', () => {
    expect(component['users']()).toHaveLength(3);
  });

  it('hides people who are already on the reservation', () => {
    expect(component['available']().map((user) => user.email)).toEqual([
      'participant1@example.com',
      'participant2@example.com',
    ]);
  });

  it('shows the organiser and does not offer to remove them', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Alpha Venue');
    expect(text).toContain('Organiser');
    expect(component['isOrganiser'](ORGANISER)).toBe(true);
  });

  it('narrows the list only after the term settles', () => {
    vi.useFakeTimers();

    component['onTermChange']('primero');

    expect(component['users']()).toHaveLength(3);

    vi.advanceTimersByTime(300);

    expect(component['users']().map((user) => user.id)).toEqual([5]);
  });

  it('shows everybody again once the search box is cleared', () => {
    vi.useFakeTimers();

    component['onTermChange']('primero');
    vi.advanceTimersByTime(300);

    component['onTermChange']('');
    vi.advanceTimersByTime(300);

    expect(component['users']()).toHaveLength(3);
  });

  it('adds a picked user to the list', () => {
    const added = capture(component);

    component['addUser'](USERS[1]);

    expect(added).toEqual([
      {
        kind: 'user',
        userId: 5,
        name: 'Primero Participant',
        email: 'participant1@example.com',
        organiser: false,
      },
    ]);
  });

  it('ignores a user who is already on the reservation', () => {
    const added = capture(component);

    component['addUser'](USERS[0]);

    expect(added).toEqual([]);
  });

  it('invites an address that belongs to nobody yet', () => {
    const added = capture(component);

    component['onEmailChange']('newcomer@example.com');
    component['invite']();

    expect(added).toEqual([{ kind: 'email', email: 'newcomer@example.com' }]);
    expect(component['email']()).toBe('');
    expect(component['emailError']()).toBeNull();
  });

  it('rejects an address that is not an address', () => {
    const added = capture(component);

    component['onEmailChange']('not-an-email');
    component['invite']();

    expect(added).toEqual([]);
    expect(component['emailError']()).toContain('valid email');
  });

  it('rejects an address that is already on the list, whichever case it is typed in', () => {
    const added = capture(component);

    component['onEmailChange']('VenueAdmin1@example.com');
    component['invite']();

    expect(added).toEqual([]);
    expect(component['emailError']()).toContain('already on the list');
  });

  it('clears a stale error as soon as the address is edited', () => {
    component['onEmailChange']('not-an-email');
    component['invite']();

    component['onEmailChange']('better@example.com');

    expect(component['emailError']()).toBeNull();
  });
});

function capture(component: ParticipantPicker): Participant[] {
  const added: Participant[] = [];
  component.add.subscribe((participant) => added.push(participant));

  return added;
}
