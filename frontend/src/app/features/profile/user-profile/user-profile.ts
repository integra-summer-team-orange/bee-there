import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import {ErrorResponse, UserRequestDto, UserResponseDto, UsersService} from '../../../../api/generated';
import { InputText } from 'primeng/inputtext';
import { Button } from 'primeng/button';

import RoleEnum = UserResponseDto.RoleEnum;
import {ConfirmationService} from 'primeng/api';
import {Router} from '@angular/router';
import {UserProfileOverlay} from '../user-profile-overlay/user-profile-overlay';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {SessionService} from '../../../core/services/session.service';

@Component({
  selector: 'app-user-profile',
  imports: [
    ReactiveFormsModule,
    InputText,
    Button,
    UserProfileOverlay,
    ConfirmDialog
  ],
  providers:[
    ConfirmationService
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile implements OnInit {

  protected user: UserResponseDto | null = null;

  protected isEditing = false;

  protected showResetPassword = false;

  protected resetPassword(): void {
    this.showResetPassword = true;
  }

  protected closeResetPassword(): void {
    this.showResetPassword = false;
  }

  protected passwordSaved(): void {
    this.showResetPassword = false;
  }

  public constructor(
    private usersService: UsersService,
    private confirmationService:ConfirmationService,
    private session:SessionService,
    private router: Router
  ) {}

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

  ngOnInit(): void {
    const userId = this.session.getUserId();
    this.usersService.getUserById(userId!).subscribe({
      next: (user) => {
        this.user = user;

        this.form.patchValue({
          name: user.name,
          email: user.email,
          phone: user.phone,
          role: user.role,
          createdAt: user.createdAt
        });
      },
      error: (error) => {
        console.error('Failed to load user profile', error);
      }
    });

    this.form.patchValue({
      name: this.user?.name,
      email: this.user?.email,
      phone: this.user?.phone,
      role: this.user?.role,
      createdAt: this.user?.createdAt
    });


  }

  protected cancelEdit(): void {
    if (!this.user) {
      return;
    }

    this.form.patchValue({
      name: this.user.name,
      email: this.user.email,
      phone: this.user.phone,
      role: this.user.role,
      createdAt: this.user.createdAt
    });

    this.isEditing = false;
  }

  protected submit(): void {
    if (this.form.invalid || !this.user) {
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
      this.user.id!,
      request
    ).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;

        this.form.patchValue({
          name: updatedUser.name,
          email: updatedUser.email,
          phone: updatedUser.phone,
          role: updatedUser.role ?? RoleEnum.Participant,
          createdAt: updatedUser.createdAt
        });

        this.isEditing = false;
      },
      error: (error) => {
        console.error('Failed to update user', error);
      }
    });
  }

  protected deleteUser(): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete user: ${this.user!.name}?<br>This action cannot be undone.`,
      header: 'Delete User',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary',
      accept: () => {
        if (!this.user!.id) {
          return;
        }

        this.usersService.deleteUser(this.user!.id).subscribe({
          next: () => {
            this.session.logout();
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
