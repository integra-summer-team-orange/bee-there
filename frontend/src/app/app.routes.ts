import {Routes} from '@angular/router';
import {Landing} from './features/landing/landing';
import {Login} from './features/login/login';
import {Register} from './features/register/register';

export const routes: Routes = [
  {
    path: '',
    component: Landing,
  },
  {
    path: 'login',
    component: Login,
  },
  {
    path: 'register',
    component: Register,
  }
];
