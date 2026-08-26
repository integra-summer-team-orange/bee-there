import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { LoginRequestDto, LoginResponseDto } from '../../shared/models/auth.model';
import { UserRequestDto, UserResponseDto } from '../../shared/models/user.model';

const TOKEN_STORAGE_KEY = 'auth_token';

/**
 * Handles authentication with the backend and persistence of the resulting JWT.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);

  private tokenSignal = signal<string | null>(this.readStoredToken());

  /**
   * Registers a new user.
   *
   * @param request the registration data
   * @return an observable emitting the created user
   */
  register(request: UserRequestDto): Observable<UserResponseDto> {
    return this.http.post<UserResponseDto>('/api/auth/register', request);
  }

  /**
   * Authenticates a user and persists the returned token.
   *
   * @param email the user's email
   * @param password the user's password
   * @param rememberMe when true, the token survives closing the browser (localStorage);
   * when false, it only lasts for the current tab/session (sessionStorage)
   * @return an observable emitting the login response
   */
  login(email: string, password: string, rememberMe: boolean): Observable<LoginResponseDto> {
    const request: LoginRequestDto = { email, password };

    return this.http
      .post<LoginResponseDto>('/api/auth/login', request)
      .pipe(tap((response) => this.setToken(response.token, rememberMe)));
  }

  /**
   * Clears the stored token, ending the session.
   */
  logout(): void {
    this.clearToken();
  }

  /**
   * Checks whether a valid, unexpired token is currently stored. Re-checked on
   * every call rather than cached, since a token valid a moment ago may have
   * expired since.
   *
   * @return true if the current session is authenticated
   */
  isAuthenticated(): boolean {
    const token = this.tokenSignal();
    if (!token) {
      return false;
    }

    if (this.isTokenExpired(token)) {
      this.clearToken();
      return false;
    }

    return true;
  }

  /**
   * @return the currently stored JWT, or null if not authenticated
   */
  getToken(): string | null {
    return this.tokenSignal();
  }

  /**
   * Reads the token from storage, discarding it if already expired.
   *
   * @return the stored token, or null if absent or expired
   */
  private readStoredToken(): string | null {
    const token = localStorage.getItem(TOKEN_STORAGE_KEY) ?? sessionStorage.getItem(TOKEN_STORAGE_KEY);

    if (token && this.isTokenExpired(token)) {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
      sessionStorage.removeItem(TOKEN_STORAGE_KEY);
      return null;
    }

    return token;
  }

  /**
   * @param token the JWT to persist
   * @param rememberMe whether to use localStorage (persistent) or sessionStorage (session-only)
   */
  private setToken(token: string, rememberMe: boolean): void {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    sessionStorage.removeItem(TOKEN_STORAGE_KEY);

    if (rememberMe) {
      localStorage.setItem(TOKEN_STORAGE_KEY, token);
    } else {
      sessionStorage.setItem(TOKEN_STORAGE_KEY, token);
    }

    this.tokenSignal.set(token);
  }

  /**
   * Removes the token from both storages and clears the in-memory signal.
   */
  private clearToken(): void {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    sessionStorage.removeItem(TOKEN_STORAGE_KEY);
    this.tokenSignal.set(null);
  }

  /**
   * Checks the JWT's `exp` claim against the current time. Does not verify the
   * token's signature — that is neither possible client-side without the secret,
   * nor necessary, since the backend independently re-validates every request.
   *
   * @param token the JWT to check
   * @return true if the token's expiry has passed or could not be read
   */
  private isTokenExpired(token: string): boolean {
    const payload = this.decodeTokenPayload(token);
    if (!payload || typeof payload.exp !== 'number') {
      return true;
    }
    return Date.now() >= payload.exp * 1000;
  }

  /**
   * @param token the JWT to decode
   * @return the decoded payload, or null if it could not be parsed
   */
  private decodeTokenPayload(token: string): { exp?: number } | null {
    try {
      const payloadSegment = token.split('.')[1];
      const base64 = payloadSegment.replace(/-/g, '+').replace(/_/g, '/');
      const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
      return JSON.parse(atob(padded));
    } catch {
      return null;
    }
  }
}
