import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import {InventoryOverview} from './features/inventory/inventory-overview/inventory-overview';
import {LandingPage} from './features/landing-page/landing-page';
import {Login} from './features/login/login';
import {Register} from './features/register/register';
import {Dashboard} from './features/dashboard/dashboard';
import {redirectIfAuthenticatedGuard} from './core/guards/redirect-if-authenticated.guard';
import {requireAuthGuard} from './core/guards/require-auth.guard';
import {UserManagement} from './features/user/user-management/user-management';
import {UserProfile} from './features/profile/user-profile/user-profile';
import {Resources} from './features/resources/resources';

export const routes: Routes = [
  {
    path: '',
    component: LandingPage,
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
    path: 'users',
    loadComponent: () =>
      import('./features/user/user-management/user-management').then(m => m.UserManagement),
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
  {
    path: 'profile',
    component: UserProfile,
    canActivate: [requireAuthGuard]
  },
  {
    path: 'venues/:id/inventory',
    loadComponent: () =>
      import('./features/inventory/inventory-overview/inventory-overview').then((m) => m.InventoryOverview),
  },
  {
    path: 'venues/:id/resources',
    component: Resources
  }
];
