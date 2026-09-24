import {Component, OnInit} from '@angular/core';
import { Avatar } from 'primeng/avatar';
import { RouterLink } from '@angular/router';
import { MenuModule } from 'primeng/menu';
import {MenuItem} from 'primeng/api';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    Avatar,
    MenuModule,
    Button,
  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {
  managementItems: MenuItem[] | undefined;

  //todo: dynamic role
  ngOnInit() {
    this.managementItems = [
      {
        label: 'User',
        icon: 'pi pi-users',
        routerLink: '/users',
      },
      {
        label: 'Venue',
        icon: 'pi pi-objects-column',
        routerLink: '/venues',
      }
    ]
  }
}
