import { Component } from '@angular/core';
import { Avatar } from 'primeng/avatar';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    Avatar
  ],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  //todo: dynamic role
}
