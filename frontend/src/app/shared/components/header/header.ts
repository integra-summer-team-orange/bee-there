import {Component, effect, signal} from '@angular/core';
import { Avatar } from 'primeng/avatar';
import { RouterLink } from '@angular/router';
import {SessionService} from '../../../core/services/session.service';
import {UsersService} from '../../../../api/generated';

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

  protected userName = signal('');

  constructor(
    protected session: SessionService,
    private usersService: UsersService
  ) {
    effect(() => {
      const userId = this.session.userId();

      if (!userId) {
        this.userName.set('');
        return;
      }

      this.usersService.getNameById(userId).subscribe({
        next: (response) => {
          this.userName.set(String(response.name));
        },
        error: (error) => {
          console.error('Failed to load user name', error);
          this.userName.set('');
        }
      });
    });
  }
}
