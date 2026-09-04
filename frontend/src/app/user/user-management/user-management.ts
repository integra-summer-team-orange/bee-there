import { Component, OnInit } from '@angular/core';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { AsyncPipe } from '@angular/common';
import {
  ErrorResponse,
  UsersService,
  UserResponseDto, PageUserResponseDto
} from '../../../api/generated';
import { UserCard } from '../user-card/user-card';
import { UserDialog } from '../user-dialog/user-dialog';
import { ConfirmationService } from 'primeng/api';
import {ConfirmDialog} from 'primeng/confirmdialog';
import {EMPTY, Observable} from 'rxjs';
import {Paginator, PaginatorState} from 'primeng/paginator';

@Component({
  selector: 'app-user-management',
  imports: [
    Button,
    InputText,
    UserCard,
    UserDialog,
    ConfirmDialog,
    AsyncPipe,
    Paginator
  ],
  providers:[
    ConfirmationService
  ],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css',
})
export class UserManagement implements OnInit {

  protected users$: Observable<PageUserResponseDto> = EMPTY;

  protected showUserDialog = false;

  public dialogMode: 'add' | 'edit' | 'details' = 'add';

  public selectedUser: UserResponseDto | null = null;

  protected pageNumber = 0;
  protected pageSize = 9;

  public constructor(
    private usersService: UsersService,
    private confirmationService:ConfirmationService
  ) {}

  public ngOnInit(): void {
    this.loadUsers();
  }

  protected loadUsers(): void {
    this.users$ = this.usersService.getAllUsers(
      this.pageNumber,
      this.pageSize
    )


  }

  protected openAddUser(): void {
    this.selectedUser = null;
    this.dialogMode = 'add';
    this.showUserDialog = true;
  }

  protected openEditUser(user: UserResponseDto): void {
    this.selectedUser = user;
    this.dialogMode = 'edit';
    this.showUserDialog = true;
  }

  protected openDetails(user: UserResponseDto): void {
    this.selectedUser = user;
    this.dialogMode = 'details';
    this.showUserDialog = true;
  }

  protected deleteUser(user: UserResponseDto): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete user: ${user.name}?<br>This action cannot be undone.`,
      header: 'Delete User',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      acceptButtonStyleClass: 'p-button-danger',
      rejectButtonStyleClass: 'p-button-secondary',
      accept: () => {
        if (!user.id) {
          return;
        }

        this.usersService.deleteUser(user.id).subscribe({
          next: () => {
            this.loadUsers();
          },
          error: (error: ErrorResponse) => {
            console.error('Failed to delete user', error);
          }
        });
      }
    });
  }

  protected closeDialog(): void {
    this.showUserDialog = false;
    this.selectedUser = null;
  }

  protected onUserSaved(): void {
    this.loadUsers();
  }

  protected onPageChange(event: PaginatorState): void {
    this.pageNumber = event.page ?? 0;
    this.pageSize = event.rows ?? 9;
    this.loadUsers();
  }
}
