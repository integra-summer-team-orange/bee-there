import { vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Observable, of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

import { PageVenueDto, VenueDto, VenuesService } from '../../../../api/generated';
import { Session } from '../../../core/services/session';
import { VenueOverview } from './venue-overview';

const MINE: VenueDto = {
  id: 1,
  managedBy: 7,
  managedByName: 'Venue Admin',
  name: 'Cluj Arena',
  address: 'Str. Stadionului 2, Cluj-Napoca',
  createdAt: '2026-06-03T10:00:00',
};

const SOMEONE_ELSES: VenueDto = {
  id: 2,
  managedBy: 9,
  managedByName: 'Another Admin',
  name: 'Sala Polivalenta',
  address: 'Aleea Stadionului 4, Cluj-Napoca',
  createdAt: '2026-06-04T10:00:00',
};

/** Stands in for the generated client so the specs never touch HTTP. */
class VenuesServiceStub {
  page: PageVenueDto = { content: [MINE, SOMEONE_ELSES], totalElements: 2 };
  searches: (string | undefined)[] = [];
  deleted: number[] = [];
  deleteError: HttpErrorResponse | null = null;

  getMine(pageNumber?: number, pageSize?: number, search?: string): Observable<PageVenueDto> {
    this.searches.push(search);

    if (!search) {
      return of(this.page);
    }

    const term = search.toLowerCase();
    const content = (this.page.content ?? []).filter((venue) =>
      venue.name?.toLowerCase().includes(term),
    );

    return of({ content, totalElements: content.length });
  }

  deleteVenue(id: number): Observable<unknown> {
    if (this.deleteError) {
      return throwError(() => this.deleteError);
    }

    this.deleted.push(id);

    return of(undefined);
  }
}

describe('VenueOverview', () => {
  let fixture: ComponentFixture<VenueOverview>;
  let venues: VenuesServiceStub;

  beforeEach(async () => {
    venues = new VenuesServiceStub();

    await TestBed.configureTestingModule({
      imports: [VenueOverview],
      providers: [
        provideRouter([]),
        MessageService,
        Session,
        { provide: VenuesService, useValue: venues },
      ],
    }).compileComponents();

    // signed in as the user managing venue 1
    TestBed.inject(Session).setToken(
      `header.${btoa(JSON.stringify({ userId: 7, role: 'VENUE_ADMIN' }))}.signature`,
    );

    fixture = TestBed.createComponent(VenueOverview);
  });

  it('renders the venues returned for the current user', async () => {
    await fixture.whenStable();

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Cluj Arena');
    expect(text).toContain('Sala Polivalenta');
  });

  it('offers delete only on the venue the user manages', async () => {
    await fixture.whenStable();

    const cards = Array.from(
      (fixture.nativeElement as HTMLElement).querySelectorAll('app-venue-card'),
    );

    expect(cards.length).toBe(2);
    expect(cards[0].textContent).toContain('Delete');
    expect(cards[1].textContent).not.toContain('Delete');
  });

  it('sends the search term to the backend instead of filtering the loaded page', async () => {
    await fixture.whenStable();

    vi.useFakeTimers();
    fixture.componentInstance['onSearchChange']('polivalenta');
    vi.advanceTimersByTime(300);
    vi.useRealTimers();
    await fixture.whenStable();

    expect(venues.searches).toContain('polivalenta');

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Sala Polivalenta');
    expect(text).not.toContain('Cluj Arena');
  });

  it('shows an empty state when the user manages nothing', async () => {
    venues.page = { content: [], totalElements: 0 };
    fixture = TestBed.createComponent(VenueOverview);
    await fixture.whenStable();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'You do not manage any venues yet.',
    );
  });

  it('deletes a venue and reloads the list', async () => {
    await fixture.whenStable();

    const component = fixture.componentInstance;
    component['askToDelete'](MINE);
    component['confirmDelete']();
    await fixture.whenStable();

    expect(venues.deleted).toEqual([1]);
    expect(component['venueToDelete']()).toBeNull();
  });

  it('reports a refused delete instead of dropping the venue', async () => {
    await fixture.whenStable();

    venues.deleteError = new HttpErrorResponse({ status: 403 });
    const messages = TestBed.inject(MessageService);
    const added: string[] = [];
    messages.messageObserver.subscribe((message) => {
      const one = Array.isArray(message) ? message[0] : message;
      added.push(String(one?.detail ?? ''));
    });

    const component = fixture.componentInstance;
    component['askToDelete'](SOMEONE_ELSES);
    component['confirmDelete']();
    await fixture.whenStable();

    expect(venues.deleted).toEqual([]);
    expect(added.some((detail) => detail.includes('only manage venues you own'))).toBe(true);
  });
});
