import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { Session } from '../../core/services/session';
import { SessionService } from '../../core/services/session.service';

@Component({
  selector: 'app-dashboard',
  imports: [ButtonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private sessionService = inject(SessionService);
  private session = inject(Session);
  private router = inject(Router);

  logout(): void {
    this.sessionService.logout();
    this.session.refreshClaims();
    this.router.navigateByUrl('/');
  }
}
