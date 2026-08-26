import { Routes } from '@angular/router';

import { Home } from './features/home/home';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'venues',
    loadComponent: () =>
      import('./features/venues/venue-overview/venue-overview').then((m) => m.VenueOverview),
  },
  {
    path: 'venues/new',
    data: { mode: 'create' },
    loadComponent: () =>
      import('./features/venues/venue-detail/venue-detail').then((m) => m.VenueDetail),
  },
  {
    path: 'venues/:id',
    data: { mode: 'view' },
    loadComponent: () =>
      import('./features/venues/venue-detail/venue-detail').then((m) => m.VenueDetail),
  },
  {
    path: 'venues/:id/edit',
    data: { mode: 'edit' },
    loadComponent: () =>
      import('./features/venues/venue-detail/venue-detail').then((m) => m.VenueDetail),
  },
];
