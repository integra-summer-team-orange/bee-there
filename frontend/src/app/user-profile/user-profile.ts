import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserRequestDto, UserResponseDto, UsersService } from '../../api/generated';
import { InputText } from 'primeng/inputtext';
import { Button } from 'primeng/button';

import RoleEnum = UserResponseDto.RoleEnum;

@Component({
  selector: 'app-user-profile',
  imports: [
    ReactiveFormsModule,
    InputText,
    Button
  ],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.css',
})
export class UserProfile implements OnInit {

  protected user: UserResponseDto | null = null;

  protected isEditing = false;

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

  constructor(
    private usersService: UsersService
  ) {}

  ngOnInit(): void {
    /*this.usersService.getUserById(1).subscribe({
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
    });*/
    this.user = {
      id: 1,
      name: 'John Doe',
      email: 'john.doe@example.com',
      phone: '0712345678',
      role: RoleEnum.Participant,
      createdAt: '2026-09-10'
    };

    this.form.patchValue({
      name: this.user.name,
      email: this.user.email,
      phone: this.user.phone,
      role: this.user.role,
      createdAt: this.user.createdAt
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

  protected resetPassword() {

  }

  protected deleteUser() {

  }
}
