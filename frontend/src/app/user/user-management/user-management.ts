import { Component, OnInit } from '@angular/core';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import {
  ErrorResponse,
  UsersService,
  UserResponseDto
} from '../../../api/generated';
import { UserCard } from '../user-card/user-card';
import { UserDialog } from '../user-dialog/user-dialog';
import { ConfirmationService } from 'primeng/api';
import {ConfirmDialog} from 'primeng/confirmdialog';

@Component({
  selector: 'app-user-management',
  imports: [
    Button,
    InputText,
    UserCard,
    UserDialog,
    ConfirmDialog
  ],
  providers:[
    ConfirmationService
  ],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css',
})
export class UserManagement implements OnInit {

  users: UserResponseDto[] = [];

  showUserDialog = false;

  dialogMode: 'add' | 'edit' | 'details' = 'add';

  selectedUser: UserResponseDto | null = null;

  pageNumber = 0;
  pageSize = 9;

  constructor(
    private usersService: UsersService,
    private confirmationService:ConfirmationService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
   /** this.usersService.getAll1(this.pageNumber, this.pageSize).subscribe({
      next: (page) => {
        this.users = page ?? [];
        console.log(page);
        console.log(page.length);
      },
      error: (error) => {
        console.error('Failed to load users', error);
      }
    });
     **/
   this.users = [
     {
       id: 1,
       name: 'Daniel',
       email: 'daniel@gmail.com',
       phone: '0747123456',
       role: UserResponseDto.RoleEnum.Admin,
       createdAt: '2026-07-21T10:00:00'
     },
     {
       id: 2,
       name: 'Maria',
       email: 'maria@gmail.com',
       phone: '0747123456',
       role: UserResponseDto.RoleEnum.VenueAdmin,
       createdAt: '2026-07-22T11:30:00'
     },
     {
       id: 3,
       name: 'Andrei',
       email: 'andrei@gmail.com',
       phone: '0747123456',
       role: UserResponseDto.RoleEnum.Participant,
       createdAt: '2026-07-23T09:15:00'
     },
     {
       id: 4,
       name: 'Ion',
       email: 'ion@gmail.com',
       phone: '0747123456',
       role: UserResponseDto.RoleEnum.Participant,
       createdAt: '2026-07-24T14:45:00'
     },
     {
       id: 5,
       name: 'Vasile',
       email: 'vasile@gmail.com',
       phone: '0747123456',
       role: UserResponseDto.RoleEnum.VenueAdmin,
       createdAt: '2026-07-25T16:20:00'
     }
   ];
  }

  openAddUser(): void {
    this.selectedUser = null;
    this.dialogMode = 'add';
    this.showUserDialog = true;
  }

  openEditUser(user: UserResponseDto): void {
    this.selectedUser = user;
    this.dialogMode = 'edit';
    this.showUserDialog = true;
  }

  openDetails(user: UserResponseDto): void {
    this.selectedUser = user;
    this.dialogMode = 'details';
    this.showUserDialog = true;
  }

  deleteUser(user: UserResponseDto): void {
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

        this.usersService.delete1(user.id).subscribe({
          next: () => {
            this.loadUsers();
          },
          error: (error) => {
            console.error('Failed to delete user', error);
          }
        });
      }
    });
  }

  closeDialog(): void {
    this.showUserDialog = false;
    this.selectedUser = null;
  }

  onUserSaved(): void {
    this.loadUsers();
  }
}
