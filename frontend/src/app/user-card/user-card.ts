import { Component, input } from '@angular/core';
import { Button } from 'primeng/button'
import {UserResponseDto} from '../../api/generated/models/userResponseDto';
@Component({
  selector: 'app-user-card',
  imports: [Button],
  templateUrl: './user-card.html',
  styleUrl: './user-card.css',
})
export class UserCard {
  user = input.required<UserResponseDto>();
}
