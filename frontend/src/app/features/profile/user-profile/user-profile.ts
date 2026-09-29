import {Component, effect} from '@angular/core';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {
  ErrorResponse,
  UserRequestDto,
  UserResponseDto,
  UsersService
} from '../../../../api/generated';
import { InputText } from 'primeng/inputtext';
import { Button } from 'primeng/button';

import RoleEnum = UserResponseDto.RoleEnum;
import { ConfirmationService } from 'primeng/api';
import { Router } from '@angular/router';
import { UserProfileOverlay } from '../user-profile-overlay/user-profile-overlay';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { UserStateService } from '../../../core/services/userState.service';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-user-profile',
  imports: [
    ReactiveFormsModule,
    InputText,
    Button,
    UserProfileOverlay,
    ConfirmDialog,
    DatePipe
  ],
  providers: [
    ConfirmationService
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile {

  protected isEditing = false;

  protected showResetPassword = false;

  constructor(
    private usersService: UsersService,
    protected userStateService: UserStateService,
    private confirmationService: ConfirmationService,
    private router: Router
  ) {
    effect(() => {
      const user = this.userStateService.user();

      if (user) {
        this.updateForm(user);
      }
    });
  }

  protected form = new FormGroup({
    name: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.maxLength(100)
      ]
    }),
    email: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.email,
        Validators.maxLength(100)
      ]
    }),
    phone: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.pattern(/^[0-9]+$/),
        Validators.maxLength(20)
      ]
    }),
    role: new FormControl<RoleEnum>(RoleEnum.Participant, {
      nonNullable: true
    }),
    createdAt: new FormControl('', {
      nonNullable: true
    })
  });

  private updateForm(user: UserResponseDto): void {
    this.form.patchValue({
      name: user.name,
      email: user.email,
      phone: user.phone,
      role: user.role ?? RoleEnum.Participant,
      createdAt: user.createdAt
    });
  }

  protected resetPassword(): void {
    this.showResetPassword = true;
  }

  protected closeResetPassword(): void {
    this.showResetPassword = false;
  }

  protected passwordSaved(): void {
    this.showResetPassword = false;
  }

  protected cancelEdit(): void {
    const user = this.userStateService.user();

    if (!user) {
      return;
    }

    this.updateForm(user);
    this.isEditing = false;
  }

  protected submit(): void {
    const user = this.userStateService.user();

    if (this.form.invalid || !user) {
      this.form.markAllAsTouched();
      return;
    }

    const formValue = this.form.getRawValue();

    const request: UserRequestDto = {
      name: formValue.name,
      email: formValue.email,
      phone: formValue.phone,
      role: formValue.role
    };

    this.usersService.updateUser(
      user.id!,
      request
    ).subscribe({
      next: (updatedUser) => {
        this.userStateService.setUser(updatedUser);

        this.isEditing = false;
      },
      error: (error) => {
        console.error('Failed to update user', error);
      }
    });
  }

  protected deleteUser(): void {
    const user = this.userStateService.user();

    if (!user?.id) {
      return;
    }

    this.confirmationService.confirm({
      message: `Are you sure you want to delete user: ${user.name}?<br>This action cannot be undone.`,
      header: 'Delete User',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary',

      accept: () => {
        this.usersService.deleteUser(user.id!).subscribe({
          next: () => {
            this.userStateService.clearUser();
            this.router.navigate(['/login']);
          },
          error: (error: ErrorResponse) => {
            console.error('Failed to delete user', error);
          }
        });
      }
    });
  }
}
