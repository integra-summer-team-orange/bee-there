import { Injectable, computed, signal } from '@angular/core';

/** Key the bearer token is stored under. The login screen writes the token under this key. */
export const TOKEN_STORAGE_KEY = 'auth_token';

/** Claims the backend puts in the JWT payload. */
interface TokenClaims {
  sub?: string;
  userId?: number;
  role?: string;
  exp?: number;
}

/** Holds the bearer token of the current user and the claims decoded from it. */
@Injectable({ providedIn: 'root' })
export class Session {
  private readonly claims = signal<TokenClaims | null>(this.decode(this.readToken()));

  /** Id of the signed-in user, or null when there is no usable token. */
  readonly userId = computed(() => this.claims()?.userId ?? null);

  /** Role of the signed-in user, or null when there is no usable token. */
  readonly role = computed(() => this.claims()?.role ?? null);

  /** Whether the signed-in user is an administrator, who may manage any venue. */
  readonly isAdmin = computed(() => this.role() === 'ADMIN');

  /**
   * Reads the stored token, or null when storage is unavailable, empty, or the token is unusable.
   *
   * A token that has expired — or that carries no expiry at all — counts as absent, so no
   * Authorization header goes out for it. Until login lands that is what makes a token left over
   * from an earlier session harmless: the request arrives without a header and the backend
   * attributes it to the development account, the same as for a browser that never had one.
   */
  readToken(): string | null {
    const token = this.readStorage();

    if (token === null || this.isExpired(token)) {
      return null;
    }

    return token;
  }

  /** Stores a token and refreshes the decoded claims. */
  setToken(token: string): void {
    try {
      localStorage.setItem(TOKEN_STORAGE_KEY, token);
    } catch {
      // storage is unavailable, but the claims below still reflect this token
    }
    this.claims.set(this.decode(token));
  }

  /** Drops the stored token. */
  clear(): void {
    try {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
      sessionStorage.removeItem(TOKEN_STORAGE_KEY);
    } catch {
      // storage is unavailable, nothing to clean up
    }
    this.claims.set(null);
  }

  /** True when the given user id is the signed-in user, or when the signed-in user is an admin. */
  canManage(ownerId: number | null | undefined): boolean {
    // RESTORE-AUTH: the backend attributes tokenless requests to an ADMIN development account.
    if (this.claims() === null) {
      return true;
    }

    if (this.isAdmin()) {
      return true;
    }

    return ownerId != null && ownerId === this.userId();
  }

  /** Reads the raw token, which the login screen may have put in either store. */
  private readStorage(): string | null {
    try {
      return localStorage.getItem(TOKEN_STORAGE_KEY) ?? sessionStorage.getItem(TOKEN_STORAGE_KEY);
    } catch {
      return null;
    }
  }

  /** Whether the token is past its expiry, or carries no expiry to check. */
  private isExpired(token: string): boolean {
    const exp = this.decode(token)?.exp;

    if (exp === undefined) {
      return true;
    }

    return Date.now() >= exp * 1000;
  }

  private decode(token: string | null): TokenClaims | null {
    if (!token) {
      return null;
    }

    const payload = token.split('.')[1];
    if (!payload) {
      return null;
    }

    try {
      const normalised = payload.replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(normalised)) as TokenClaims;
    } catch {
      return null;
    }
  }
}
