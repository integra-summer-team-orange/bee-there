import { Routes } from '@angular/router';

import { BookingApi } from './booking-api';
import { MockBookingApi } from './booking-mock';

export const BOOKING_ROUTES: Routes = [
  {
    path: '',
    providers: [{ provide: BookingApi, useClass: MockBookingApi }],
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./reservation-overview/reservation-overview').then((m) => m.ReservationOverview),
      },
      {
        path: ':id',
        loadComponent: () =>
          import('./reservation-detail/reservation-detail').then((m) => m.ReservationDetail),
      },
      {
        path: ':id/items',
        loadComponent: () =>
          import('./reservation-items/reservation-items').then((m) => m.ReservationItems),
      },
    ],
  },
];
