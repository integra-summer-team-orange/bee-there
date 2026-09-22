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

  it('counts what the venue already has out plus what this reservation takes', async () => {
    expect(component['alreadyReserved']()).toBe(7);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('7/25');

    fixture.componentRef.setInput('quantity', 4);
    await fixture.whenStable();

    expect(component['reserved']()).toBe(11);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('11/25');
  });

  it('moves the bar with the quantity', async () => {
    expect(component['reservedShare']()).toBeCloseTo(7 / 25);

    fixture.componentRef.setInput('quantity', 4);
    await fixture.whenStable();

    expect(component['reservedShare']()).toBeCloseTo(11 / 25);
  });

  it('colours the bar green, then amber, then red as the stock fills', async () => {
    expect(component['levelColor']()).toBe('var(--color-level-low)');

    fixture.componentRef.setInput('quantity', 6);
    await fixture.whenStable();

    expect(component['reservedShare']()).toBeCloseTo(13 / 25);
    expect(component['levelColor']()).toBe('var(--color-level-medium)');

    fixture.componentRef.setInput('quantity', 12);
    await fixture.whenStable();

    expect(component['reservedShare']()).toBeCloseTo(19 / 25);
    expect(component['levelColor']()).toBe('var(--color-level-high)');
  });

  it('never reports more than a full bar', async () => {
    fixture.componentRef.setInput('item', { ...BALLS, totalQuantity: 10, availableQuantity: 10 });
    fixture.componentRef.setInput('quantity', 10);
    await fixture.whenStable();

    expect(component['reservedShare']()).toBe(1);
  });

  it('paints the selected fill only once something is taken', async () => {
    expect(component['cardStyle']()).toEqual({});

    fixture.componentRef.setInput('quantity', 1);
    await fixture.whenStable();

    expect(component['cardStyle']()).toEqual({
      backgroundColor: 'var(--color-selected-container)',
      borderColor: 'var(--color-selected-outline)',
    });
  });

  it('clamps a quantity above what is available', () => {
    const emitted = capture(component);

    component['onQuantityChange'](99);

    expect(emitted).toEqual([BALLS.availableQuantity]);
  });

  it('clamps a negative quantity, and treats a cleared box as none', () => {
    const emitted = capture(component);

    component['onQuantityChange'](-3);
    component['onQuantityChange'](null);

    expect(emitted).toEqual([0, 0]);
  });

  it('passes a quantity within range straight through', () => {
    const emitted = capture(component);

    component['onQuantityChange'](4);

    expect(emitted).toEqual([4]);
  });
});

function capture(component: ItemCard): number[] {
  const emitted: number[] = [];
  component.quantityChange.subscribe((quantity) => emitted.push(quantity));

  return emitted;
}
