import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';

import { BookingApi } from '../booking-api';
import { MockBookingApi } from '../booking-mock';
import { ReservationOverview } from './reservation-overview';

describe('ReservationOverview', () => {
  let fixture: ComponentFixture<ReservationOverview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationOverview],
      providers: [provideRouter([]), MessageService, { provide: BookingApi, useClass: MockBookingApi }],
    }).compileComponents();

    fixture = TestBed.createComponent(ReservationOverview);
    await settle(fixture);
  });

  function headings(): string[] {
    return Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('h2')).map(
      (heading) => heading.textContent?.trim() ?? '',
    );
  }

  it('groups the upcoming reservations by venue', () => {
    expect(headings()).toEqual(['Cluj Arena', 'Lakeside Community Center']);
  });

  it('shows the first reservation until another is chosen', async () => {
    expect(fixture.componentInstance['selected']()?.id).toBe(1);

    fixture.componentInstance['selectedId'].set(4);
    await fixture.whenStable();

    expect(fixture.componentInstance['selected']()?.resourceName).toBe('Lakeside Volleyball Court');
  });

  it('removes a cancelled reservation from the list', async () => {
    fixture.componentInstance['toCancel'].set(fixture.componentInstance['bookings']()[0]);
    fixture.componentInstance['confirmCancel']();
    await settle(fixture);
    await settle(fixture);

    expect(fixture.componentInstance['bookings']().map((booking) => booking.id)).not.toContain(1);
  });

  it('searches after the user stops typing', async () => {
    fixture.componentInstance['onSearchChange']('lakeside');
    await settle(fixture);
    await settle(fixture);

    expect(headings()).toEqual(['Lakeside Community Center']);
  });
});

async function settle(fixture: ComponentFixture<unknown>): Promise<void> {
  await new Promise((resolve) => setTimeout(resolve, 350));
  await fixture.whenStable();
}
