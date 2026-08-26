import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router, provideRouter } from '@angular/router';
import { MessageService } from 'primeng/api';
import { Observable, of } from 'rxjs';

import {
  InventoryDto,
  InventoryService,
  Page,
  ResourcesService,
  VenueDto,
  VenuesService,
} from '../../../../api/generated';
import { Session } from '../../../core/services/session';
import { VenueDetail, VenueDetailMode } from './venue-detail';

const VENUE: VenueDto = {
  id: 1,
  managedBy: 7,
  managedByName: 'Venue Admin',
  name: 'Cluj Arena',
  description: 'Stadium with tens of facilities available for renting',
  address: 'Str. Stadionului 2, Cluj-Napoca',
  createdAt: '2026-06-03T10:00:00',
};

class VenuesServiceStub {
  created: unknown[] = [];
  updated: { id: number; body: unknown }[] = [];

  getById(): Observable<VenueDto> {
    return of(VENUE);
  }

  create(body: unknown): Observable<VenueDto> {
    this.created.push(body);

    return of({ ...VENUE, id: 42 });
  }

  update(id: number, body: unknown): Observable<VenueDto> {
    this.updated.push({ id, body });

    return of(VENUE);
  }
}

class ResourcesServiceStub {
  getAll2(): Observable<Page> {
    return of({ content: [{ id: 1, venueId: 1, name: 'Court A', capacity: 20 }] } as Page);
  }
}

class InventoryServiceStub {
  items: InventoryDto[] = [
    { id: 1, venueId: 1, name: 'Basketballs', totalQuantity: 60, availableQuantity: 40 },
    { id: 2, venueId: 2, name: 'Elsewhere', totalQuantity: 10, availableQuantity: 0 },
  ];

  getAll4(): Observable<InventoryDto[]> {
    return of(this.items);
  }
}

async function setup(mode: VenueDetailMode, id?: string) {
  const venues = new VenuesServiceStub();

  await TestBed.configureTestingModule({
    imports: [VenueDetail],
    providers: [
      provideRouter([]),
      MessageService,
      Session,
      { provide: VenuesService, useValue: venues },
      { provide: ResourcesService, useValue: new ResourcesServiceStub() },
      { provide: InventoryService, useValue: new InventoryServiceStub() },
      {
        provide: ActivatedRoute,
        useValue: {
          snapshot: {
            data: { mode },
            paramMap: { get: () => id ?? null },
          },
        },
      },
    ],
  }).compileComponents();

  TestBed.inject(Session).setToken(
    `header.${btoa(JSON.stringify({ userId: 7, role: 'VENUE_ADMIN' }))}.signature`,
  );

  // the specs assert on where the component wants to go rather than letting the router navigate
  const navigate = vi.spyOn(TestBed.inject(Router), 'navigate').mockResolvedValue(true);

  const fixture: ComponentFixture<VenueDetail> = TestBed.createComponent(VenueDetail);
  await fixture.whenStable();

  return { fixture, venues, navigate };
}

describe('VenueDetail', () => {
  afterEach(() => TestBed.resetTestingModule());

  it('shows the venue read-only in view mode', async () => {
    const { fixture } = await setup('view', '1');
    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('Cluj Arena');
    expect(element.querySelector('#venue-address')?.hasAttribute('readonly')).toBe(true);
    expect(element.textContent).not.toContain('Add Venue');
  });

  it('summarises the venue own resources and inventory', async () => {
    const { fixture } = await setup('view', '1');
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Court A');
    expect(text).toContain('Basketballs');
    // 40 of 60 available means a third is booked; the item belonging to another venue is ignored
    expect(text).toContain('Booked 33%');
    expect(text).not.toContain('Elsewhere');
  });

  it('makes the fields editable in edit mode and sends an update', async () => {
    const { fixture, venues } = await setup('edit', '1');
    const component = fixture.componentInstance;

    expect(
      (fixture.nativeElement as HTMLElement).querySelector('#venue-address')?.hasAttribute(
        'readonly',
      ),
    ).toBe(false);

    component['form'].setValue({
      name: 'Renamed Arena',
      description: 'Updated',
      address: 'Str. Noua 1',
    });
    component['save']();
    await fixture.whenStable();

    expect(venues.updated).toEqual([
      {
        id: 1,
        body: { name: 'Renamed Arena', description: 'Updated', address: 'Str. Noua 1' },
      },
    ]);
  });

  it('creates a venue and navigates to it', async () => {
    const { fixture, venues, navigate } = await setup('create');
    const component = fixture.componentInstance;

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Add Venue');

    component['form'].setValue({
      name: 'New Arena',
      description: '',
      address: 'Str. Noua 2',
    });
    component['save']();
    await fixture.whenStable();

    expect(venues.created).toEqual([
      { name: 'New Arena', description: '', address: 'Str. Noua 2' },
    ]);
    expect(navigate).toHaveBeenCalledWith(['/venues', 42]);
  });

  it('keeps the edit screen read-only for a venue the user does not manage', async () => {
    const { fixture, venues } = await setup('edit', '1');
    TestBed.inject(Session).setToken(
      `header.${btoa(JSON.stringify({ userId: 99, role: 'VENUE_ADMIN' }))}.signature`,
    );
    await fixture.whenStable();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('You can only edit venues you manage.');
    expect(element.querySelector('#venue-address')?.hasAttribute('readonly')).toBe(true);

    fixture.componentInstance['save']();
    await fixture.whenStable();

    expect(venues.updated).toEqual([]);
  });

  it('refuses to save an empty form', async () => {
    const { fixture, venues } = await setup('create');
    const component = fixture.componentInstance;

    component['save']();
    await fixture.whenStable();

    expect(venues.created).toEqual([]);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'A name of up to 150 characters is required.',
    );
  });
});
