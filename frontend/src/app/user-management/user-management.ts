import {Component, OnInit} from '@angular/core';
import {Button} from 'primeng/button';
import {TableModule} from 'primeng/table';
import {InputText} from 'primeng/inputtext';
import {ErrorResponse, UserResponseDto} from '../../api/generated';
import {UserControllerService} from '../../api/generated';
import {UserCard} from '../user-card/user-card';

@Component({
  selector: 'app-user-management',
  imports: [
    Button,
    TableModule,
    InputText,
    UserCard
  ],
  templateUrl: './user-management.html',
  styleUrl: './user-management.css',
})
export class UserManagement implements OnInit{
  users:UserResponseDto[] = [];

  constructor(private userControllerService: UserControllerService) {
  }

  ngOnInit():void{
    this.userControllerService.getAll1().subscribe({
      next: (users) => {
        this.users = users;
        console.log(users);
      },
      error: (error:ErrorResponse)=>{
        console.error('Failed to load users',error)
      }
    })
  }
}
