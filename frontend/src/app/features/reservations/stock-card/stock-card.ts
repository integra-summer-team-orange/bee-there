import { Component, computed, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CardModule } from 'primeng/card';
import { InputNumberModule } from 'primeng/inputnumber';

import { StockItem } from '../booking-api';

/** One inventory item with how much of it is out, and a stepper for how many this reservation takes. */
@Component({
  selector: 'app-stock-card',
  imports: [FormsModule, CardModule, InputNumberModule],
  templateUrl: './stock-card.html',
  styleUrl: './stock-card.css',
})
export class StockCard {
  readonly item = input.required<StockItem>();
  readonly quantity = input(0);
  /** How many of this item the reservation already holds, which `availableQuantity` has already subtracted. */
  readonly held = input(0);
  readonly quantityChange = output<number>();

  protected readonly max = computed(() => this.item().availableQuantity + this.held());

  protected readonly reserved = computed(
    () => this.item().totalQuantity - this.max() + this.quantity(),
  );

  protected readonly share = computed(() =>
    this.item().totalQuantity ? (this.reserved() / this.item().totalQuantity) * 100 : 0,
  );

  protected readonly barColour = computed(() => {
    const share = this.share();
    if (share < 50) {
      return 'var(--color-level-low)';
    }

    return share < 75 ? 'var(--color-level-medium)' : 'var(--color-level-high)';
  });

  protected readonly cardStyle = computed(() =>
    this.quantity() > 0
      ? {
          background: 'var(--color-selected-container)',
          borderColor: 'var(--color-selected-outline)',
        }
      : {},
  );
}
