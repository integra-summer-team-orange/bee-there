import {Injectable, Signal, signal} from '@angular/core';

const TOKEN_KEY = 'auth_token';
const USER_ID='user_id';

@Injectable({ providedIn: 'root' })
export class SessionService {

  private _userId = signal<number | null>(this.getUserId());

  public userId = this._userId.asReadonly();

  saveToken(token: string, rememberMe: boolean): void {
    if (rememberMe) {
      localStorage.setItem(TOKEN_KEY, token);
    } else {
      sessionStorage.setItem(TOKEN_KEY, token);
    }

  }

  saveUserId(id: number | undefined, rememberMe: boolean): void{
    if(rememberMe){
      localStorage.setItem(USER_ID, String(id));
    } else {
      sessionStorage.setItem(USER_ID,String(id));
    }
    this._userId.set(Number(id));
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(TOKEN_KEY);

    localStorage.removeItem(USER_ID);
    sessionStorage.removeItem(USER_ID);
    this._userId.set(null);
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

  getUserId(): number | null {
    return Number(localStorage.getItem(USER_ID)) || Number(sessionStorage.getItem(USER_ID));
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
