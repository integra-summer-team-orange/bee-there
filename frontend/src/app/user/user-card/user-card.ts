import { Component, input, output } from '@angular/core';
import { Button } from 'primeng/button';
import { UserResponseDto } from '../../../api/generated';

@Component({
  selector: 'app-user-card',
  imports: [Button],
  templateUrl: './user-card.html',
  styleUrl: './user-card.css',
})
export class UserCard {
  user = input.required<UserResponseDto>();

  edit = output<UserResponseDto>();
  details = output<UserResponseDto>();
  delete = output<UserResponseDto>();

  onEdit(): void {
    this.edit.emit(this.user());
  }

  onDetails(): void {
    this.details.emit(this.user());
  }

  onDelete(): void {
    this.delete.emit(this.user());
  }
}
