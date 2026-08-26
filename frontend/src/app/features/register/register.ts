import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { switchMap } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../core/services/auth.service';
import { UserRequestDto } from '../../shared/models/user.model';

function passwordsMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  return password === confirmPassword ? null : { passwordMismatch: true };
}

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink, ButtonModule, InputTextModule, MessageModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  errorMessage = signal<string | null>(null);

  form = this.fb.group(
    {
      name: ['', [Validators.required]],
      phone: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]],
    },
    { validators: passwordsMatchValidator },
  );

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.errorMessage.set(
        this.form.hasError('passwordMismatch') ? 'Passwords do not match' : null,
      );
      return;
    }

    this.errorMessage.set(null);
    this.loading.set(true);

    const { name, phone, email, password } = this.form.getRawValue();

    const request: UserRequestDto = {
      name: name!,
      email: email!,
      password: password!,
      phone: phone!,
      role: 'PARTICIPANT',
    };

    this.authService
      .register(request)
      .pipe(switchMap(() => this.authService.login(email!, password!, true)))
      .subscribe({
        next: () => {
          this.loading.set(false);
          this.router.navigateByUrl('/home');
        },
        error: (err: HttpErrorResponse) => {
          this.loading.set(false);
          this.errorMessage.set(this.extractErrorMessage(err));
        },
      });
  }

  private extractErrorMessage(err: HttpErrorResponse): string {
    const messages = err.error?.messages;
    if (Array.isArray(messages) && messages.length > 0) {
      return messages.join(' ');
    }
    return 'Something went wrong. Please try again.';
  }

  fieldError(controlName: string, label: string): string | null {
    const control = this.form.get(controlName);
    if (!control || !control.touched) {
      return null;
    }
    if (control.hasError('required')) {
      return `${label} is required`;
    }
    if (control.hasError('email')) {
      return 'Invalid email format';
    }
    if (controlName === 'confirmPassword' && this.form.hasError('passwordMismatch') && control.value) {
      return 'Passwords do not match';
    }
    return null;
  }
}
