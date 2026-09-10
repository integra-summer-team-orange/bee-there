import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import {Resources} from './features/resources/resources';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'venues/:id/resources',
    component: Resources
  }
];
