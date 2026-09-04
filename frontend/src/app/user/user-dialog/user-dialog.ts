import { Component, input, output, OnChanges, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Button } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { InputText } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import {
  UsersService,
  UserRequestDto,
  UserResponseDto
} from '../../../api/generated';

import RoleEnum = UserResponseDto.RoleEnum;

@Component({
  selector: 'app-user-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    Button,
    Dialog,
    InputText,
    Select
  ],
  templateUrl: './user-dialog.html',
  styleUrl: './user-dialog.css',
})
export class UserDialog implements OnChanges {

  public visible = input(false);

  public mode = input<'add' | 'edit' | 'details'>('add');

  public user = input<UserResponseDto | null>(null);

  protected closed = output<void>();

  protected saved = output<void>();

  protected roles = [
    { label: 'Admin', value: RoleEnum.Admin },
    { label: 'Venue Admin', value: RoleEnum.VenueAdmin },
    { label: 'Participant', value: RoleEnum.Participant }
  ];

  protected form = new FormGroup({
    name: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    }),

    email: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.email]
    }),

    phone: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    }),

    role: new FormControl<RoleEnum>(RoleEnum.Participant, {
      nonNullable: true,
      validators: [Validators.required]
    })
  });

  public constructor(
    private usersService: UsersService
  ) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['user'] || changes['mode']) {
      this.updateForm();
    }
  }

  private updateForm(): void {
    if (this.mode() === 'add') {
      this.form.reset({
        name: '',
        email: '',
        phone: '',
        role: RoleEnum.Participant
      });

      return;
    }

    if (this.mode() === 'edit' && this.user()) {
      const user = this.user()!;

      this.form.patchValue({
        name: user.name ?? '',
        email: user.email ?? '',
        phone: user.phone ?? '',
        role: user.role ?? RoleEnum.Participant
      });
    }
  }

  protected close(): void {
    this.closed.emit();
  }

  protected submit(): void {
    if (this.mode() === 'details') {
      return;
    }

    if (this.form.invalid) {
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

    if (this.mode() === 'add') {
      this.usersService.inviteUser(request).subscribe({
        next: () => {
          this.saved.emit();
          this.close();
        },
        error: (error) => {
          console.error('Failed to create user', error);
        }
      });

      return;
    }

    if (this.mode() === 'edit' && this.user()) {
      this.usersService.updateUser(
        this.user()!.id!,
        request
      ).subscribe({
        next: () => {
          this.saved.emit();
          this.close();
        },
        error: (error) => {
          console.error('Failed to update user', error);
        }
      });
    }
  }
}
