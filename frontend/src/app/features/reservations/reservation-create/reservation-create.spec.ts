import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Observable, of, throwError } from 'rxjs';

import {
  BookedSlot,
  CreatedReservation,
  InventoryOption,
  Participant,
  ReservationApi,
  ReservationDraft,
  ResourceOption,
  UserOption,
  VenueOption,
} from '../reservation-models';
import { ReservationCreate } from './reservation-create';

class ResizeObserverStub {
  observe(): void {}
  unobserve(): void {}
  disconnect(): void {}
}

globalThis.ResizeObserver ??= ResizeObserverStub as unknown as typeof ResizeObserver;

const VENUE: VenueOption = { id: 1, name: 'Cluj Arena', address: 'Aleea Stadionului 2' };

const COURT: ResourceOption = {
  id: 1,
  venueId: 1,
  name: 'Main Basketball Court',
  activityType: 'Basketball',
  type: 'INDOOR_SPORT',
  capacity: 30,
  hourlyRate: 55,
};

const BALLS: InventoryOption = {
  id: 1,
  venueId: 1,
  name: 'Basketballs',
  totalQuantity: 25,
  availableQuantity: 18,
};

const ORGANISER: UserOption = { id: 3, name: 'Alpha Venue', email: 'venueadmin1@example.com' };

const GUEST: UserOption = { id: 5, name: 'Primero Participant', email: 'participant1@example.com' };

class ReservationApiStub extends ReservationApi {
  submitted: ReservationDraft[] = [];
  createError: HttpErrorResponse | null = null;
  booked: BookedSlot[] = [];

  listVenues(): Observable<VenueOption[]> {
    return of([VENUE]);
  }

  listResources(venueId: number): Observable<ResourceOption[]> {
    return of([COURT].filter((resource) => resource.venueId === venueId));
  }

  listInventory(venueId: number): Observable<InventoryOption[]> {
    return of([BALLS].filter((item) => item.venueId === venueId));
  }

  listBookedSlots(): Observable<BookedSlot[]> {
    return of(this.booked);
  }

  listUsers(): Observable<UserOption[]> {
    return of([ORGANISER, GUEST]);
  }

  organiser(): Observable<UserOption> {
    return of(ORGANISER);
  }

  createReservation(draft: ReservationDraft): Observable<CreatedReservation> {
    if (this.createError) {
      return throwError(() => this.createError);
    }

    this.submitted.push(draft);

    return of({ id: 42, start: draft.start, end: draft.end, status: 'PENDING' as const });
  }
}

describe('ReservationCreate', () => {
  let fixture: ComponentFixture<ReservationCreate>;
  let component: ReservationCreate;
  let api: ReservationApiStub;

  beforeEach(async () => {
    api = new ReservationApiStub();

    await TestBed.configureTestingModule({
      imports: [ReservationCreate],
      providers: [provideRouter([]), MessageService, { provide: ReservationApi, useValue: api }],
    }).compileComponents();

    fixture = TestBed.createComponent(ReservationCreate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('renders every step of the flow', async () => {
    setPeriod(component, 10);
    await fixture.whenStable();

    expect(textOf(fixture)).toContain('Select booking period');

    component['next']();
    await fixture.whenStable();

    expect(textOf(fixture)).toContain('Select a resource');

    await selectCourt(component);
    await fixture.whenStable();

    expect(textOf(fixture)).toContain('Main Basketball Court');

    component['next']();
    await fixture.whenStable();

    expect(textOf(fixture)).toContain('Book items');
    expect(textOf(fixture)).toContain('Basketballs');

    component['next']();
    await fixture.whenStable();

    expect(textOf(fixture)).toContain('Add participants');
    expect(textOf(fixture)).toContain('Alpha Venue');

    component['next']();
    await fixture.whenStable();

    expect(textOf(fixture)).toContain('Review booking');
    expect(textOf(fixture)).toContain('Cluj Arena');
  });

  it('follows the completion meter the design puts on each step', async () => {
    expect(component['progress']()).toBe(5);

    setPeriod(component, 10);
    component['next']();

    expect(component['progress']()).toBe(25);

    await selectCourt(component);
    component['next']();

    expect(component['progress']()).toBe(50);

    component['next']();

    expect(component['progress']()).toBe(75);

    await fillInDraft(component);
    component['submit']();

    expect(component['progress']()).toBe(100);
  });

  it('shows the confirmation instead of the form once it is booked', async () => {
    await fillInDraft(component);

    component['submit']();
    await fixture.whenStable();

    expect(textOf(fixture)).toContain('Booking confirmed!');
    expect(textOf(fixture)).toContain('Thank you for booking with BeeThere!');
  });

  it('puts the organiser on the reservation without being asked', () => {
    const participants = component['participants']();

    expect(participants).toHaveLength(1);
    expect(participants[0]).toEqual({
      kind: 'user',
      userId: ORGANISER.id,
      name: ORGANISER.name,
      email: ORGANISER.email,
      organiser: true,
    });
  });

  it('locks the end exactly one hour after the start', () => {
    setPeriod(component, 9);

    expect(component['endAt']()?.getHours()).toBe(10);
    expect(component['durationHours']()).toBe(1);

    setPeriod(component, 22);

    expect(component['endAt']()?.getHours()).toBe(23);
  });

  it('refuses to leave the first step until a start is picked', () => {
    component['startAt'].set(null);

    expect(component['periodValid']()).toBe(false);

    component['next']();

    expect(component['step']()).toBe(1);
  });

  it('advances once the period is valid', () => {
    setPeriod(component, 10);

    component['next']();

    expect(component['step']()).toBe(2);
  });

  it('lets the user step back, but never past the first step', () => {
    setPeriod(component, 10);
    component['next']();

    component['back']();
    component['back']();

    expect(component['step']()).toBe(1);
  });

  it('prices the booking from the resource rate and the fixed hour', async () => {
    setPeriod(component, 10);
    await selectCourt(component);

    expect(component['durationHours']()).toBe(1);
    expect(component['estimatedRate']()).toBe(55);
  });

  it('blocks a slot that clashes with an existing booking', async () => {
    api.booked = [{ resourceId: COURT.id, start: iso(10, 30), end: iso(11, 30) }];
    setPeriod(component, 10);

    await selectCourt(component);

    expect(component['clashingSlots']()).toHaveLength(1);
    expect(component['stepComplete'](2)).toBe(false);
    expect(component['stepComplete'](5)).toBe(false);

    component['submit']();

    expect(component['submitting']()).toBe(false);
    expect(component['created']()).toBeNull();
  });

  it('treats a booking that starts when another ends as no clash', async () => {
    api.booked = [{ resourceId: COURT.id, start: iso(11, 0), end: iso(12, 0) }];
    setPeriod(component, 10);

    await selectCourt(component);

    expect(component['clashingSlots']()).toHaveLength(0);
  });

  it('turns item quantities into object rental rows, skipping the untouched ones', async () => {
    await fillInDraft(component);
    component['setQuantity'](BALLS, 4);

    component['submit']();

    expect(api.submitted[0].items).toEqual([{ inventoryId: BALLS.id, quantity: 4 }]);
  });

  it('drops an item again when its quantity goes back to zero', async () => {
    await fillInDraft(component);
    component['setQuantity'](BALLS, 4);
    component['removeItem'](BALLS.id);

    component['submit']();

    expect(api.submitted[0].items).toEqual([]);
  });

  it('submits existing users and email invites in one participant list', async () => {
    await fillInDraft(component);

    component['addParticipant']({
      kind: 'user',
      userId: GUEST.id,
      name: GUEST.name,
      email: GUEST.email,
      organiser: false,
    });
    component['addParticipant']({ kind: 'email', email: 'newcomer@example.com' });

    component['submit']();

    const draft = api.submitted[0];

    expect(draft.resourceId).toBe(COURT.id);
    expect(draft.participants.map(kindAndEmail)).toEqual([
      ['user', ORGANISER.email],
      ['user', GUEST.email],
      ['email', 'newcomer@example.com'],
    ]);
    expect(component['created']()?.id).toBe(42);
  });

  it('ignores somebody who is already on the list', () => {
    component['addParticipant']({ kind: 'email', email: 'VenueAdmin1@example.com' });

    expect(component['participants']()).toHaveLength(1);
  });

  it('removes an invited person but never the organiser', () => {
    const invite: Participant = { kind: 'email', email: 'newcomer@example.com' };
    component['addParticipant'](invite);

    component['removeParticipant'](invite);
    component['removeParticipant'](component['participants']()[0]);

    expect(component['participants']().map((participant) => participant.email)).toEqual([
      ORGANISER.email,
    ]);
  });

  it('does not submit without a start', async () => {
    await fillInDraft(component);
    component['startAt'].set(null);

    component['submit']();

    expect(api.submitted).toHaveLength(0);
  });

  it('submits the derived hour as the end', async () => {
    await fillInDraft(component);

    component['submit']();

    const draft = api.submitted[0];

    expect(new Date(draft.end).getTime() - new Date(draft.start).getTime()).toBe(3_600_000);
  });

  it('names a participant by their name, or by their address when they have no account', () => {
    expect(component['labelOf']({ kind: 'email', email: 'newcomer@example.com' })).toBe(
      'newcomer@example.com',
    );
    expect(component['labelOf'](component['participants']()[0])).toBe(ORGANISER.name);
  });

  it('explains a rejected booking instead of failing silently', async () => {
    api.createError = new HttpErrorResponse({
      status: 409,
      error: { messages: ['The resource is already booked from 10:00 to 11:00.'] },
    });
    await fillInDraft(component);

    component['submit']();

    expect(component['created']()).toBeNull();
    expect(component['submitError']()).toContain('already taken');
    expect(component['submitError']()).toContain('10:00');
  });

  it('keeps the organiser when starting another reservation', async () => {
    await fillInDraft(component);
    component['addParticipant']({ kind: 'email', email: 'newcomer@example.com' });
    component['setQuantity'](BALLS, 2);
    component['submit']();

    component['createAnother']();

    expect(component['created']()).toBeNull();
    expect(component['step']()).toBe(1);
    expect(component['rentedItems']()).toEqual([]);
    expect(component['participants']()).toHaveLength(1);
    expect(component['participants']()[0].email).toBe(ORGANISER.email);
  });
});

function textOf(fixture: ComponentFixture<ReservationCreate>): string {
  return (fixture.nativeElement as HTMLElement).textContent ?? '';
}

function at(hour: number, minute = 0): Date {
  const date = new Date();
  date.setHours(hour, minute, 0, 0);

  return date;
}

function iso(hour: number, minute = 0): string {
  return at(hour, minute).toISOString();
}

function setPeriod(component: ReservationCreate, startHour: number): void {
  component['startAt'].set(at(startHour));
}

async function selectCourt(component: ReservationCreate): Promise<void> {
  component['onVenueChange'](VENUE.id);
  component['selectResource'](COURT);
}

async function fillInDraft(component: ReservationCreate): Promise<void> {
  setPeriod(component, 10);
  await selectCourt(component);
  component['step'].set(5);
}

function kindAndEmail(participant: Participant): [string, string] {
  return [participant.kind, participant.email];
}
