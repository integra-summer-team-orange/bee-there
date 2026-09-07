import {Routes} from '@angular/router';
import {Home} from './features/home/home';
import {InventoryOverview} from './features/inventory/inventory-overview/inventory-overview';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'inventory',
    component: InventoryOverview
  }
];
