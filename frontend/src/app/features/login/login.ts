import { Component, inject, signal } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { InputTextModule } from 'primeng/inputtext';
import { AuthenticationService, LoginRequestDto } from '../../../api/generated';
import { SessionService } from '../../core/services/session.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, ReactiveFormsModule, ButtonModule, CheckboxModule, InputTextModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private api = inject(AuthenticationService);
  private session = inject(SessionService);
  private router = inject(Router);

  rememberMe = new FormControl(false);
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', Validators.required);

  errorMessage = signal<string | null>(null);

  onSubmit(): void {
    this.errorMessage.set(null);

    if (this.email.invalid || this.password.invalid) {
      this.email.markAsTouched();
      this.password.markAsTouched();
      return;
    }

    const request: LoginRequestDto = {
      email: this.email.value ?? '',
      password: this.password.value ?? '',
    };

    this.api.login(request).subscribe({
      next: (response) => {
        if (!response.token) {
          this.errorMessage.set('Invalid email or password.');
          return;
        }

        this.session.saveToken(response.token, this.rememberMe.value ?? false);
        this.router.navigateByUrl('/dashboard');
      },
      error: () => {
        this.errorMessage.set('Invalid email or password.');
      },
    });
  }
}
