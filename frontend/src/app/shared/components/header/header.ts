import {Component, effect} from '@angular/core';
import { Avatar } from 'primeng/avatar';
import { RouterLink } from '@angular/router';
import {SessionService} from '../../../core/services/session.service';
import {UsersService} from '../../../../api/generated';
import {UserStateService} from '../../../core/services/userState.service';

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

  constructor(
    protected session: SessionService,
    private usersService: UsersService,
    protected userState: UserStateService
  ) {
    effect(() => {
      const userId = this.session.userId();

      if (!userId) {
        this.userState.clearUser();
        return;
      }

      this.usersService.getUserById(userId).subscribe({
        next: (user) => {
          this.userState.setUser(user);
        },
        error: (error) => {
          console.error('Failed to load user', error);
          this.userState.clearUser();
        }
      });
    });
  }
}
