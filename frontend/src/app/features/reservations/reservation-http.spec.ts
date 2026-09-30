import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { firstValueFrom } from 'rxjs';

import { Session } from '../../core/services/session';
import { HttpReservationApi } from './reservation-http';

describe('HttpReservationApi', () => {
  let api: HttpReservationApi;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        HttpReservationApi,
        { provide: Session, useValue: { userId: () => 4 } },
      ],
    });

    api = TestBed.inject(HttpReservationApi);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('sends the booking as local wall-clock time, without a zone', async () => {
    const start = new Date(2026, 9, 5, 14, 0, 0);
    const end = new Date(2026, 9, 5, 15, 0, 0);

    const created = firstValueFrom(
      api.createReservation({
        venueId: 2,
        resourceId: 7,
        inventoryId: 9,
        start: start.toISOString(),
        end: end.toISOString(),
        maxParticipants: 15,
        participants: [],
      }),
    );

    const request = http.expectOne((req) => req.url.endsWith('/api/reservations'));

    expect(request.request.body).toEqual({
      venueId: 2,
      resourceId: 7,
      inventoryId: 9,
      startTime: '2026-10-05T14:00:00',
      endTime: '2026-10-05T15:00:00',
      maxParticipants: 15,
    });

    request.flush({ id: 1, startTime: '2026-10-05T14:00:00', endTime: '2026-10-05T15:00:00', status: 'ACTIVE' });

    expect((await created).status).toBe('ACTIVE');
  });

  it('filters the user list by name or email', async () => {
    const found = firstValueFrom(api.listUsers('beta'));

    http.expectOne((req) => req.url.endsWith('/api/users')).flush({
      content: [
        { id: 3, name: 'Alpha Venue', email: 'venueadmin1@example.com' },
        { id: 4, name: 'Beta Venue', email: 'venueadmin2@example.com' },
      ],
    });

    expect((await found).map((user) => user.id)).toEqual([4]);
  });

  it('loads the organiser from the signed-in user id', async () => {
    const organiser = firstValueFrom(api.organiser());

    http
      .expectOne((req) => req.url.endsWith('/api/users/4'))
      .flush({ id: 4, name: 'Beta Venue', email: 'venueadmin2@example.com' });

    expect(await organiser).toEqual({ id: 4, name: 'Beta Venue', email: 'venueadmin2@example.com' });
  });
});
