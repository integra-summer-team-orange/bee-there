import { HttpRequest } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AUTH_DISABLED, Session } from '../services/session';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

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

  it('ignores a token left in storage while authorization is switched off', () => {
    // RESTORE-AUTH: a stale token used to be attached to every request, which made the backend attribute
    // the call to that user instead of the seeded development account — "my venues" then came back empty
    // with nothing on screen to explain why. This expectation flips once login lands.
    TestBed.inject(Session).setToken(
      `header.${btoa(JSON.stringify({ userId: 99, role: 'ADMIN' }))}.signature`,
    );

    expect(AUTH_DISABLED).toBe(true);
    expect(sendThrough().headers.has('Authorization')).toBe(false);
  });
});
