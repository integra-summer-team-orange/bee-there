import {Component, effect, computed, inject, OnInit} from '@angular/core';
import { Avatar } from 'primeng/avatar';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter, map } from 'rxjs';
import { Session } from '../../../core/services/session';
import { SessionService } from '../../../core/services/session.service';
import { UsersService } from '../../../../api/generated';
import { UserStateService } from '../../../core/services/userState.service';
import { MenuModule } from 'primeng/menu';
import { MenuItem } from 'primeng/api';
import {toSignal} from '@angular/core/rxjs-interop';
import {Button} from 'primeng/button';

type HeaderVariant = 'landing' | 'login' | 'register' | 'app';


function publicVariantForUrl(url: string): HeaderVariant {
  switch (url.split('?')[0]) {
    case '/login':
      return 'login';
    case '/register':
      return 'register';
    default:
      return 'landing';
  }
}

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
  private readonly router = inject(Router);
  private readonly usersService = inject(UsersService);
  private readonly sessionService = inject(SessionService);
  protected readonly session = inject(Session);
  protected readonly userState = inject(UserStateService);

  private readonly url = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map((event) => event.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );


  protected readonly isAuthenticated = computed(() => this.session.role() !== null);


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

  protected readonly variant = computed<HeaderVariant>(() =>
    this.isAuthenticated() ? 'app' : publicVariantForUrl(this.url()),
  );

  constructor() {
    effect(() => {
      const userId = this.sessionService.userId();

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
        },
      });
    });
  }
}
