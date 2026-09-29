import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryOption } from '../reservation-models';
import { ItemCard } from './item-card';

const BALLS: InventoryOption = {
  id: 1,
  venueId: 1,
  name: 'Basketballs',
  totalQuantity: 25,
  availableQuantity: 18,
};

describe('ItemCard', () => {
  let fixture: ComponentFixture<ItemCard>;
  let component: ItemCard;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [ItemCard] }).compileComponents();

    fixture = TestBed.createComponent(ItemCard);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('item', BALLS);
    await fixture.whenStable();
  });

  it('counts what the venue already has out plus the one this reservation takes', async () => {
    expect(component['reserved']()).toBe(7);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('7/25');

    fixture.componentRef.setInput('selected', true);
    await fixture.whenStable();

    expect(component['reserved']()).toBe(8);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('8/25');
  });

  it('colours the bar green, then amber, then red as the stock fills', async () => {
    expect(component['levelColor']()).toBe('var(--color-level-low)');

    fixture.componentRef.setInput('item', { ...BALLS, availableQuantity: 12 });
    await fixture.whenStable();

    expect(component['levelColor']()).toBe('var(--color-level-medium)');

    fixture.componentRef.setInput('item', { ...BALLS, availableQuantity: 5 });
    await fixture.whenStable();

    expect(component['levelColor']()).toBe('var(--color-level-high)');
  });

  it('never reports more than a full bar', async () => {
    fixture.componentRef.setInput('item', { ...BALLS, totalQuantity: 10, availableQuantity: 0 });
    fixture.componentRef.setInput('selected', true);
    await fixture.whenStable();

    expect(component['reservedShare']()).toBe(1);
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

  it('asks for itself to be chosen when clicked', () => {
    const chosen: InventoryOption[] = [];
    component.choose.subscribe((item) => chosen.push(item));

    (fixture.nativeElement as HTMLElement).querySelector('button')?.click();

    expect(chosen).toEqual([BALLS]);
  });

  it('cannot be chosen once none are left', async () => {
    fixture.componentRef.setInput('item', { ...BALLS, availableQuantity: 0 });
    await fixture.whenStable();

    const button = (fixture.nativeElement as HTMLElement).querySelector('button');

    expect(button?.disabled).toBe(true);
  });
});
