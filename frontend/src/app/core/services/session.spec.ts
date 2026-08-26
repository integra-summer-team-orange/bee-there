import { TestBed } from '@angular/core/testing';

import { Session } from './session';

/** Builds a token whose payload carries the given claims. The signature is never checked client-side. */
function tokenWith(claims: Record<string, unknown>): string {
  return `header.${btoa(JSON.stringify(claims))}.signature`;
}

describe('Session', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('reports no user when nothing is stored', () => {
    const session = TestBed.inject(Session);

    expect(session.readToken()).toBeNull();
    expect(session.userId()).toBeNull();
    expect(session.role()).toBeNull();
  });

  it('lets an anonymous session manage anything while there is no login screen', () => {
    const session = TestBed.inject(Session);

    // RESTORE-AUTH: this expectation flips to false once login lands
    expect(session.canManage(123)).toBe(true);
  });

  it('decodes the user id and role out of a stored token', () => {
    const session = TestBed.inject(Session);
    session.setToken(tokenWith({ userId: 7, role: 'VENUE_ADMIN' }));

    expect(session.userId()).toBe(7);
    expect(session.role()).toBe('VENUE_ADMIN');
    expect(session.isAdmin()).toBe(false);
  });

  it('survives a token that is not decodable', () => {
    const session = TestBed.inject(Session);
    session.setToken('not-a-jwt');

    expect(session.userId()).toBeNull();
  });

  it('lets a user manage only their own venues', () => {
    const session = TestBed.inject(Session);
    session.setToken(tokenWith({ userId: 7, role: 'VENUE_ADMIN' }));

    expect(session.canManage(7)).toBe(true);
    expect(session.canManage(8)).toBe(false);
    expect(session.canManage(null)).toBe(false);
  });

  it('lets an admin manage every venue', () => {
    const session = TestBed.inject(Session);
    session.setToken(tokenWith({ userId: 1, role: 'ADMIN' }));

    expect(session.isAdmin()).toBe(true);
    expect(session.canManage(999)).toBe(true);
  });

  it('picks up a token set after construction', () => {
    const session = TestBed.inject(Session);

    session.setToken(tokenWith({ userId: 3, role: 'PARTICIPANT' }));

    expect(session.userId()).toBe(3);

    session.clear();

    expect(session.userId()).toBeNull();
  });
});
