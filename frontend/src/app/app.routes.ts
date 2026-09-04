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
  }
];
