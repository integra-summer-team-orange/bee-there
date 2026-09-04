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

  public user = input.required<UserResponseDto>();

  protected edit = output<UserResponseDto>();
  protected details = output<UserResponseDto>();
  protected delete = output<UserResponseDto>();

  protected onEdit(): void {
    this.edit.emit(this.user());
  }

  protected onDetails(): void {
    this.details.emit(this.user());
  }

  protected onDelete(): void {
    this.delete.emit(this.user());
  }
}
