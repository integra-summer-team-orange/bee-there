import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';

import { BookingApi } from '../booking-api';
import { MockBookingApi } from '../booking-mock';
import { ReservationDetail } from './reservation-detail';

describe('ReservationDetail', () => {
  let fixture: ComponentFixture<ReservationDetail>;
  let routeId = '1';

  async function open(id: string): Promise<void> {
    routeId = id;
    vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(ReservationDetail);
    await settle(fixture);
  }

  function text(): string {
    return (fixture.nativeElement as HTMLElement).textContent ?? '';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationDetail],
      providers: [
        provideRouter([]),
        MessageService,
        { provide: BookingApi, useClass: MockBookingApi },
        {
          provide: ActivatedRoute,
          useFactory: () => ({ snapshot: { paramMap: convertToParamMap({ id: routeId }) } }),
        },
      ],
    }).compileComponents();
  });

  it('shows the organiser without a Remove button, then the participants', async () => {
    await open('1');

    const rows = Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('li'));

    expect(rows[0].textContent).toContain('Beta Venue');
    expect(rows[0].textContent).toContain('Organizer');
    expect(rows[0].textContent).not.toContain('Remove');
    expect(rows[1].textContent).toContain('Primero Participant');
  });

  it('accepts every request at once', async () => {
    await open('1');

    fixture.componentInstance['tab'].set('requests');
    fixture.componentInstance['approve'](fixture.componentInstance['booking']()!.requests);
    await settle(fixture);

    expect(fixture.componentInstance['booking']()!.requests).toEqual([]);
    expect(fixture.componentInstance['booking']()!.participants).toHaveLength(7);
  });

  it('shows why a user could not be added', async () => {
    await open('1');

    fixture.componentInstance['openAddUser']();
    fixture.componentInstance['newEmail'].set('nobody@example.com');
    fixture.componentInstance['addUser']();
    await settle(fixture);

    expect(fixture.componentInstance['addError']()).toContain('nobody@example.com');
  });

  it('returns to the overview after cancelling', async () => {
    await open('1');

    fixture.componentInstance['confirmCancel']();
    await settle(fixture);

    expect(TestBed.inject(Router).navigate).toHaveBeenCalledWith(['/reservations'], expect.anything());
  });

  it('says so when the reservation does not exist', async () => {
    await open('999');

    expect(text()).toContain('This reservation could not be found.');
  });
});

async function settle(fixture: ComponentFixture<unknown>): Promise<void> {
  await new Promise((resolve) => setTimeout(resolve, 300));
  await fixture.whenStable();
}
