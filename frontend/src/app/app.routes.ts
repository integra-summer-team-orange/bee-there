import {Routes} from '@angular/router';
import {Landing} from './features/landing/landing';
import {Login} from './features/login/login';
import {Register} from './features/register/register';
import {Dashboard} from './features/dashboard/dashboard';
import {redirectIfAuthenticatedGuard} from './core/guards/redirect-if-authenticated.guard';
import {requireAuthGuard} from './core/guards/require-auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: Landing,
    canActivate: [redirectIfAuthenticatedGuard],
  },
  {
    path: 'login',
    component: Login,
    canActivate: [redirectIfAuthenticatedGuard],
  },
  {
    path: 'register',
    component: Register,
    canActivate: [redirectIfAuthenticatedGuard],
  },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [requireAuthGuard],
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
