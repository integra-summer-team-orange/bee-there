import { Component, input, output } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import { Button } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { InputText } from 'primeng/inputtext';
import {UserResponseDto, UsersService} from '../../../../api/generated';

@Component({
  selector: 'app-user-profile-overlay',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    Button,
    Dialog,
    InputText
  ],
  templateUrl: './user-profile-overlay.html',
  styleUrl: './user-profile-overlay.css',
})
export class UserProfileOverlay {

  public visible = input(false);

  public user = input<UserResponseDto | null>(null);

  protected closed = output<void>();

  protected saved = output<void>();

  protected form = new FormGroup({
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    }),

    confirmPassword: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    })
  });

  public constructor(
    private usersService: UsersService
  ) {

    this.form.controls.password.valueChanges.subscribe(() => {
      this.form.controls.confirmPassword.updateValueAndValidity();
    });

    this.form.controls.confirmPassword.addValidators(
      this.passwordsMatch
    );

  }

  private passwordsMatch = (
    control: AbstractControl
  ): ValidationErrors | null => {
    return control.value === this.form.controls.password.value
      ? null
      : { mismatch: true };
  };

  protected close(): void {
    this.form.reset();
    this.closed.emit();
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.getRawValue();

    this.usersService.updatePassword(
     this.user()!.id!,
     { password: formValue.password }
    ).subscribe({
     next: () => {
       this.saved.emit();
       this.close();
     },
     error: (error) => {
       console.error('Failed to reset password', error);
     }
    });

    this.saved.emit();
    this.close();
  }
}
