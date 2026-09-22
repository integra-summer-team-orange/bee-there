import { Injectable, signal } from '@angular/core';
import {UserResponseDto} from '../../../api/generated';

@Injectable({
  providedIn: 'root'
})
export class UserStateService {

  user = signal<UserResponseDto | null>(null);

  setUser(user: UserResponseDto): void {
    this.user.set(user);
  }

  clearUser(): void {
    this.user.set(null);
  }
}
