import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StockItem } from '../booking-api';
import { StockCard } from './stock-card';

const BASKETBALLS: StockItem = { id: 1, name: 'Basketballs', totalQuantity: 20, availableQuantity: 10 };

describe('StockCard', () => {
  let fixture: ComponentFixture<StockCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [StockCard] }).compileComponents();

    fixture = TestBed.createComponent(StockCard);
    fixture.componentRef.setInput('item', BASKETBALLS);
  });

  function text(): string {
    return (fixture.nativeElement as HTMLElement).textContent ?? '';
  }

  it('counts what is already out when nothing is chosen', async () => {
    await fixture.whenStable();

    expect(text()).toContain('Reserved: 10/20');
  });

  it('adds the chosen quantity to what is out', async () => {
    fixture.componentRef.setInput('quantity', 6);
    await fixture.whenStable();

    expect(text()).toContain('Reserved: 16/20');
  });

  it('does not count this reservation twice when it already holds some', async () => {
    fixture.componentRef.setInput('held', 4);
    fixture.componentRef.setInput('quantity', 4);
    await fixture.whenStable();

    expect(text()).toContain('Reserved: 10/20');
  });

  it('turns the bar red past three quarters', async () => {
    fixture.componentRef.setInput('quantity', 6);
    await fixture.whenStable();

    const bar = (fixture.nativeElement as HTMLElement).querySelector<HTMLElement>('[style*="width"]');

    expect(bar?.style.width).toBe('80%');
    expect(bar?.style.background).toContain('--color-level-high');
  });
});
