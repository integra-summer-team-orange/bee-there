import { Component, computed, input, output } from '@angular/core';
import { CardModule } from 'primeng/card';

import { InventoryOption } from '../reservation-models';

const MEDIUM_LEVEL = 0.5;
const HIGH_LEVEL = 0.75;

@Component({
  selector: 'app-item-card',
  imports: [CardModule],
  templateUrl: './item-card.html',
  styleUrl: './item-card.css',
})
export class ItemCard {
  readonly item = input.required<InventoryOption>();

  readonly selected = input<boolean>(false);

  readonly choose = output<InventoryOption>();

  protected readonly alreadyReserved = computed(
    () => this.item().totalQuantity - this.item().availableQuantity,
  );

  protected readonly reserved = computed(() => this.alreadyReserved() + (this.selected() ? 1 : 0));

  protected readonly soldOut = computed(() => this.item().availableQuantity <= 0);

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

  protected readonly cardClass = computed(() =>
    [
      'h-full wrap-anywhere border shadow-md transition-colors',
      this.selected()
        ? 'border-transparent'
        : this.soldOut()
          ? 'border-outline-variant opacity-60'
          : 'border-outline-variant hover:border-general-highlight',
    ].join(' '),
  );

  protected readonly cardStyle = computed(() =>
    this.selected()
      ? {
          backgroundColor: 'var(--color-selected-container)',
          borderColor: 'var(--color-selected-outline)',
        }
      : {},
  );
}
