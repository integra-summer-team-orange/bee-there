import { HttpRequest } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { Session, TOKEN_STORAGE_KEY } from '../services/session';
import { authInterceptor } from './auth.interceptor';

/** Builds a token that expires the given number of seconds from now. */
function tokenExpiringIn(offsetSeconds: number): string {
  const exp = Math.floor(Date.now() / 1000) + offsetSeconds;

  return `header.${btoa(JSON.stringify({ userId: 99, role: 'ADMIN', exp }))}.signature`;
}

describe('authInterceptor', () => {
  beforeEach(() => {
    localStorage.clear();
    sessionStorage.clear();
    TestBed.configureTestingModule({});
  });

  function sendThrough(): HttpRequest<unknown> {
    let seen!: HttpRequest<unknown>;

    TestBed.runInInjectionContext(() => {
      authInterceptor(new HttpRequest('GET', '/api/venues/my'), (request) => {
        seen = request;

        return of();
      }).subscribe();
    });

    return seen;
  }

  it('sends no Authorization header when there is no token', () => {
    expect(sendThrough().headers.has('Authorization')).toBe(false);
  });

  it('attaches a valid token', () => {
    const token = tokenExpiringIn(3600);
    TestBed.inject(Session).setToken(token);

    expect(sendThrough().headers.get('Authorization')).toBe(`Bearer ${token}`);
  });

  it('ignores a token that has expired', () => {
    // RESTORE-AUTH: a token left over from an earlier session must not outlive its expiry,
    // otherwise the request is attributed to that user instead of the development account.
    localStorage.setItem(TOKEN_STORAGE_KEY, tokenExpiringIn(-60));

    expect(sendThrough().headers.has('Authorization')).toBe(false);
  });
});
