import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequestDto, LoginResponseDto } from '../../shared/models/auth.model';
import { UserRequestDto, UserResponseDto } from '../../shared/models/user.model';

const TOKEN_KEY = 'auth_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  register(request: UserRequestDto): Observable<UserResponseDto> {
    return this.http.post<UserResponseDto>('/api/auth/register', request);
  }

  login(email: string, password: string, rememberMe: boolean): Observable<LoginResponseDto> {
    const request: LoginRequestDto = { email, password };

    return this.http.post<LoginResponseDto>('/api/auth/login', request).pipe(
      tap((response) => {
        this.saveToken(response.token, rememberMe);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_KEY);
  }

  isAuthenticated(): boolean {
    const token = this.getToken();

    if (!token) {
      return false;
    }

    if (this.isExpired(token)) {
      this.logout();
      return false;
    }

    return true;
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY) || sessionStorage.getItem(TOKEN_KEY);
  }

  private saveToken(token: string, rememberMe: boolean): void {
    if (rememberMe) {
      localStorage.setItem(TOKEN_KEY, token);
    } else {
      sessionStorage.setItem(TOKEN_KEY, token);
    }
  }

  private isExpired(token: string): boolean {
    try {
      const payload = this.decodePayload(token);

      if (!payload.exp) {
        return true;
      }

      return Date.now() >= payload.exp * 1000;
    } catch {
      return true;
    }
  }

  private decodePayload(token: string): { exp?: number } {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');

    return JSON.parse(atob(padded));
  }
}
