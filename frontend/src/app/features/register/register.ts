import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormControl, FormsModule, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { AuthenticationService, LoginRequestDto, UserRequestDto, UserResponseDto } from '../../../api/generated';
import { SessionService } from '../../core/services/session.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, ReactiveFormsModule, ButtonModule, InputTextModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private api = inject(AuthenticationService);
  private session = inject(SessionService);
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
      role: UserResponseDto.RoleEnum.Participant,
    };

    this.api.register(request).subscribe({
      next: () => {
        const credentials: LoginRequestDto = {
          email: request.email,
          password: this.password.value ?? '',
        };

        this.api.login(credentials).subscribe({
          next: (response) => {
            if (!response.token) {
              this.errorMessage.set('Account created, but automatic sign-in failed. Please log in.');
              this.router.navigateByUrl('/login');
              return;
            }

            this.session.saveToken(response.token, false);
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
