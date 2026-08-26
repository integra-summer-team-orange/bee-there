import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { VenueDto } from '../../../../api/generated';
import { VenueCard } from './venue-card';

const VENUE: VenueDto = {
  id: 4,
  managedBy: 7,
  managedByName: 'Venue Admin',
  name: 'Cluj Arena',
  description: 'Stadium with tens of facilities available for renting',
  address: 'Str. Stadionului 2, Cluj-Napoca',
  createdAt: '2026-06-03T10:00:00',
};

describe('VenueCard', () => {
  let fixture: ComponentFixture<VenueCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VenueCard],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(VenueCard);
    fixture.componentRef.setInput('venue', VENUE);
  });

  it('shows the venue, its owner and its address', async () => {
    await fixture.whenStable();

    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Cluj Arena');
    expect(text).toContain('Venue Admin');
    expect(text).toContain('Str. Stadionului 2, Cluj-Napoca');
  });

  it('offers only Details when the venue is not manageable', async () => {
    await fixture.whenStable();

    const labels = buttonLabels(fixture);

    expect(labels).toContain('Details');
    expect(labels).not.toContain('Delete');
  });

  it('offers edit and delete when the venue is manageable', async () => {
    fixture.componentRef.setInput('manageable', true);
    await fixture.whenStable();

    expect(buttonLabels(fixture)).toContain('Delete');
    expect(
      (fixture.nativeElement as HTMLElement).querySelector('[aria-label="Edit venue"]'),
    ).not.toBeNull();
  });

  it('emits the venue when delete is pressed', async () => {
    fixture.componentRef.setInput('manageable', true);
    await fixture.whenStable();

    let emitted: VenueDto | undefined;
    fixture.componentInstance.remove.subscribe((venue) => (emitted = venue));

    deleteButton(fixture).click();
    await fixture.whenStable();

    expect(emitted).toEqual(VENUE);
  });
});

function buttons(fixture: ComponentFixture<VenueCard>): HTMLButtonElement[] {
  return Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('button'));
}

function buttonLabels(fixture: ComponentFixture<VenueCard>): string[] {
  return buttons(fixture).map((button) => button.textContent?.trim() ?? '');
}

function deleteButton(fixture: ComponentFixture<VenueCard>): HTMLButtonElement {
  const found = buttons(fixture).find((button) => button.textContent?.includes('Delete'));

  if (!found) {
    throw new Error('the delete button is not rendered');
  }

  return found;
}
