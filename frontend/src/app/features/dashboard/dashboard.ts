import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { SessionService } from '../../core/services/session.service';

@Component({
  selector: 'app-dashboard',
  imports: [ButtonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {
  private session = inject(SessionService);
  private router = inject(Router);

  logout(): void {
    this.session.logout();
    this.router.navigateByUrl('/');
  }
}
