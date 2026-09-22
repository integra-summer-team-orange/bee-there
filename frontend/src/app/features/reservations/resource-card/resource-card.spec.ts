import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourceOption } from '../reservation-models';
import { ResourceCard } from './resource-card';

const COURT: ResourceOption = {
  id: 1,
  venueId: 1,
  name: 'Main Basketball Court',
  activityType: 'Basketball',
  activityDescription: 'Hardwood indoor full-sized court',
  type: 'INDOOR_SPORT',
  capacity: 30,
  hourlyRate: 55,
};

describe('ResourceCard', () => {
  let fixture: ComponentFixture<ResourceCard>;
  let component: ResourceCard;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [ResourceCard] }).compileComponents();

    fixture = TestBed.createComponent(ResourceCard);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('resource', COURT);
    await fixture.whenStable();
  });

  it('shows every field the design labels', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Main Basketball Court');
    expect(text).toContain('Activity Type:');
    expect(text).toContain('Basketball');
    expect(text).toContain('Description:');
    expect(text).toContain('Hardwood indoor full-sized court');
    expect(text).toContain('Capacity:');
    expect(text).toContain('30');
    expect(text).toContain('55.00/hr');
  });

  it('renders the stored resource type as words', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(text).toContain('Indoor sport');
    expect(text).not.toContain('INDOOR_SPORT');
  });

  it('leaves the description out when there is none', async () => {
    fixture.componentRef.setInput('resource', { ...COURT, activityDescription: undefined });
    await fixture.whenStable();

    expect((fixture.nativeElement as HTMLElement).textContent).not.toContain('Description:');
  });

  it('paints the selected fill only when it is the chosen one', async () => {
    expect(component['cardStyle']()).toEqual({});

    fixture.componentRef.setInput('selected', true);
    await fixture.whenStable();

    expect(component['cardStyle']()).toEqual({
      backgroundColor: 'var(--color-selected-container)',
      borderColor: 'var(--color-selected-outline)',
    });
  });

  it('marks itself pressed for assistive technology when selected', async () => {
    fixture.componentRef.setInput('selected', true);
    await fixture.whenStable();

    const button = (fixture.nativeElement as HTMLElement).querySelector('button');

    expect(button?.getAttribute('aria-pressed')).toBe('true');
  });

  it('asks for itself to be chosen when clicked', () => {
    const chosen: ResourceOption[] = [];
    component.choose.subscribe((resource) => chosen.push(resource));

    (fixture.nativeElement as HTMLElement).querySelector('button')?.click();

    expect(chosen).toEqual([COURT]);
  });
});
