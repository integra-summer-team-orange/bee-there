import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormControl, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { AuthService } from '../../core/services/auth.service';
import { UserRequestDto } from '../../shared/models/user.model';

@Component({
  selector: 'app-register',
  imports: [FormsModule, ReactiveFormsModule, ButtonModule, InputTextModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private authService = inject(AuthService);
  private router = inject(Router);

  name = new FormControl('', Validators.required);
  phone = new FormControl('', [Validators.required, Validators.pattern(/^\+?\d{4,16}$/)]);
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', Validators.required);

  passwordsMatch = (control: AbstractControl): ValidationErrors | null => {
    return control.value === this.password.value ? null : { mismatch: true };
  };

  confirmPassword = new FormControl('', [Validators.required, this.passwordsMatch]);

  errorMessage = signal<string | null>(null);

  constructor() {
    this.password.valueChanges.subscribe(() => this.confirmPassword.updateValueAndValidity());
  }

  onSubmit(): void {
    this.errorMessage.set(null);

    if (
      this.name.invalid ||
      this.phone.invalid ||
      this.email.invalid ||
      this.password.invalid ||
      this.confirmPassword.invalid
    ) {
      this.name.markAsTouched();
      this.phone.markAsTouched();
      this.email.markAsTouched();
      this.password.markAsTouched();
      this.confirmPassword.markAsTouched();
      return;
    }

    const request: UserRequestDto = {
      name: this.name.value ?? '',
      email: this.email.value ?? '',
      password: this.password.value ?? '',
      phone: this.phone.value ?? '',
      role: 'PARTICIPANT',
    };

    this.authService.register(request).subscribe({
      next: () => {
        this.authService.login(request.email, request.password, false).subscribe({
          next: () => {
            this.router.navigateByUrl('/dashboard');
          },
          error: () => {
            this.errorMessage.set('Account created, but automatic sign-in failed. Please log in.');
            this.router.navigateByUrl('/login');
          },
        });
      },
      error: () => {
        this.errorMessage.set('Registration failed. Please check your details and try again.');
      },
    });
  }
}
