import { Injectable, computed, signal } from '@angular/core';

/** Key the bearer token is stored under. */
export const TOKEN_STORAGE_KEY = 'integra.token';

/**
 * RESTORE-AUTH: while the backend has authorization switched off there is no login screen to produce a
 * token, and a token left in storage from earlier testing would silently change who the backend thinks you
 * are — requests would be attributed to that user instead of the seeded development account, and "my venues"
 * would come back empty for no visible reason. So stored tokens are ignored outright until login lands.
 * Setting this to false restores normal token handling.
 */
export const AUTH_DISABLED = true;

/** Claims the backend puts in the JWT payload. */
interface TokenClaims {
  sub?: string;
  userId?: number;
  role?: string;
  exp?: number;
}

/**
 * Holds the bearer token of the current user and the claims decoded from it.
 *
 * Once the authentication feature lands this service is the single place that has to change.
 */
@Injectable({ providedIn: 'root' })
export class Session {
  private readonly claims = signal<TokenClaims | null>(this.decode(this.readToken()));

  /** Id of the signed-in user, or null when there is no usable token. */
  readonly userId = computed(() => this.claims()?.userId ?? null);

  /** Role of the signed-in user, or null when there is no usable token. */
  readonly role = computed(() => this.claims()?.role ?? null);

  /** Whether the signed-in user is an administrator, who may manage any venue. */
  readonly isAdmin = computed(() => this.role() === 'ADMIN');

  /** Reads the stored token. Returns null when storage is unavailable, empty, or auth is switched off. */
  readToken(): string | null {
    if (AUTH_DISABLED) {
      return null;
    }

    try {
      return localStorage.getItem(TOKEN_STORAGE_KEY);
    } catch {
      return null;
    }
  }

  /** Stores a token and refreshes the decoded claims. */
  setToken(token: string): void {
    try {
      localStorage.setItem(TOKEN_STORAGE_KEY, token);
    } catch {
      // storage is unavailable; the claims below still reflect this token for the current page
    }
    this.claims.set(this.decode(token));
  }

  /** Drops the stored token. */
  clear(): void {
    try {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
    } catch {
      // nothing to clean up when storage is unavailable
    }
    this.claims.set(null);
  }

  /** True when the given user id is the signed-in user, or when the signed-in user is an admin. */
  canManage(ownerId: number | null | undefined): boolean {
    // RESTORE-AUTH: an anonymous session may manage anything, matching the backend not enforcing ownership.
    // Hiding every action would otherwise leave nothing to develop against.
    if (this.claims() === null) {
      return true;
    }

    if (this.isAdmin()) {
      return true;
    }

    return ownerId != null && ownerId === this.userId();
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
