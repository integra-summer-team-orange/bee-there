import { Routes } from '@angular/router';

import { ReservationCreate } from './reservation-create/reservation-create';
import { HttpReservationApi } from './reservation-http';
import { ReservationApi } from './reservation-models';

export const RESERVATION_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'new',
  },
  {
    path: 'new',
    providers: [{ provide: ReservationApi, useClass: HttpReservationApi }],
    component: ReservationCreate,
  },
];
