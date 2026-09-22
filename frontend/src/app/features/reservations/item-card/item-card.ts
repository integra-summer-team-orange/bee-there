import { Component, computed, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { InputNumberModule } from 'primeng/inputnumber';

import { InventoryOption } from '../reservation-models';

const MEDIUM_LEVEL = 0.5;
const HIGH_LEVEL = 0.75;

@Component({
  selector: 'app-item-card',
  imports: [FormsModule, CardModule, InputNumberModule],
  templateUrl: './item-card.html',
  styleUrl: './item-card.css',
})
export class ItemCard {
  readonly item = input.required<InventoryOption>();

  readonly quantity = input<number>(0);

  readonly quantityChange = output<number>();

  protected readonly alreadyReserved = computed(
    () => this.item().totalQuantity - this.item().availableQuantity,
  );

  protected readonly reserved = computed(() => this.alreadyReserved() + this.quantity());

  protected readonly reservedShare = computed(() => {
    const total = this.item().totalQuantity;

    return total > 0 ? Math.min(1, this.reserved() / total) : 0;
  });

  protected readonly levelColor = computed(() => {
    const share = this.reservedShare();

    if (share >= HIGH_LEVEL) {
      return 'var(--color-level-high)';
    }

    return share >= MEDIUM_LEVEL ? 'var(--color-level-medium)' : 'var(--color-level-low)';
  });

  protected readonly taken = computed(() => this.quantity() > 0);

  protected readonly cardClass = computed(() =>
    [
      'h-full wrap-anywhere border shadow-md transition-colors',
      this.taken() ? 'border-transparent' : 'border-outline-variant',
    ].join(' '),
  );

  protected readonly cardStyle = computed(() =>
    this.taken()
      ? {
          backgroundColor: 'var(--color-selected-container)',
          borderColor: 'var(--color-selected-outline)',
        }
      : {},
  );

  protected onQuantityChange(value: number | null): void {
    const wanted = value ?? 0;
    const clamped = Math.max(0, Math.min(wanted, this.item().availableQuantity));

    this.quantityChange.emit(clamped);
  }
}
