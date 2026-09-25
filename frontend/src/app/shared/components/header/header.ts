import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter, map } from 'rxjs';
import { Avatar } from 'primeng/avatar';
import { Button } from 'primeng/button';
import { UserResponseDto } from '../../../../api/generated';
import { Session } from '../../../core/services/session';


type HeaderVariant = 'landing' | 'login' | 'register' | 'app';

interface NavItem {
  label: string;
  path: string;
  /** Roles allowed to see this item; omit to show it to every signed-in user. */
  roles?: UserResponseDto.RoleEnum[];
}

const NAV_ITEMS: NavItem[] = [
  { label: 'Dashboard', path: '/dashboard' },
  {
    label: 'Management',
    path: '/management',
    roles: [UserResponseDto.RoleEnum.Admin, UserResponseDto.RoleEnum.VenueAdmin],
  },
  { label: 'Venues', path: '/venues' },
  {
    label: 'My Reservations',
    path: '/reservations',
    roles: [UserResponseDto.RoleEnum.Participant],
  },
  { label: 'Contact', path: '/contact' },
];

/** Picks which public (logged-out) header to show. Anything unrecognized falls back to landing. */
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
  imports: [RouterLink, Avatar, Button],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  private readonly router = inject(Router);
  protected readonly session = inject(Session);

  private readonly url = toSignal(
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd),
      map((event) => event.urlAfterRedirects),
    ),
    { initialValue: this.router.url },
  );

  /** Whether there's a signed-in user. Checked first: only unauthenticated visitors care which public page they're on. */
  protected readonly isAuthenticated = computed(() => this.session.role() !== null);

  protected readonly variant = computed<HeaderVariant>(() =>
    this.isAuthenticated() ? 'app' : publicVariantForUrl(this.url()),
  );

  protected readonly navItems = computed(() => {
    const role = this.session.role();
    return NAV_ITEMS.filter((item) => !item.roles || item.roles.some((r) => r === role));
  });

  protected readonly avatarLabel = computed(() => {
    const role = this.session.role();
    return role ? role.charAt(0) : '?';
  });
}
