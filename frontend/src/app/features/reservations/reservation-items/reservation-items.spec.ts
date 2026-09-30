import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';
import { firstValueFrom } from 'rxjs';

import { BookingApi } from '../booking-api';
import { MockBookingApi } from '../booking-mock';
import { ReservationItems } from './reservation-items';

describe('ReservationItems', () => {
  let fixture: ComponentFixture<ReservationItems>;
  let api: BookingApi;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationItems],
      providers: [
        provideRouter([]),
        MessageService,
        { provide: BookingApi, useClass: MockBookingApi },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { paramMap: convertToParamMap({ id: '1' }) } },
        },
      ],
    }).compileComponents();

    api = TestBed.inject(BookingApi);
    vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);
    fixture = TestBed.createComponent(ReservationItems);
    await settle(fixture);
  });

  it('starts from the item the reservation already holds', () => {
    expect(fixture.componentInstance['choice']()).toEqual({ inventoryId: 1, quantity: 4 });
    expect(fixture.componentInstance['changed']()).toBe(false);
  });

  it('replaces the chosen item when another one gets a quantity', () => {
    const cones = fixture.componentInstance['stock']().find((item) => item.id === 2)!;

    fixture.componentInstance['setQuantity'](cones, 3);

    expect(fixture.componentInstance['choice']()).toEqual({ inventoryId: 2, quantity: 3 });
    expect(fixture.componentInstance['changed']()).toBe(true);
  });

  it('drops the item when its quantity goes back to zero', () => {
    const basketballs = fixture.componentInstance['stock']().find((item) => item.id === 1)!;

    fixture.componentInstance['setQuantity'](basketballs, 0);

    expect(fixture.componentInstance['choice']()).toBeNull();
  });

  it('saves the choice and returns to the reservation', async () => {
    const cones = fixture.componentInstance['stock']().find((item) => item.id === 2)!;
    fixture.componentInstance['setQuantity'](cones, 3);

    fixture.componentInstance['save']();
    await settle(fixture);

    const booking = await firstValueFrom(api.get(1));
    expect(booking.item?.inventoryId).toBe(2);
    expect(TestBed.inject(Router).navigate).toHaveBeenCalledWith(['/reservations', 1], expect.anything());
  });
});

async function settle(fixture: ComponentFixture<unknown>): Promise<void> {
  await new Promise((resolve) => setTimeout(resolve, 600));
  await fixture.whenStable();
}
