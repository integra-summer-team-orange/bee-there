import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ToastModule } from 'primeng/toast';
import { switchMap, tap } from 'rxjs';

import { Booking, BookingApi, ItemChoice, StockItem } from '../booking-api';
import { describeBookingError } from '../booking-error';
import { Notice } from '../booking-notice';
import { StockCard } from '../stock-card/stock-card';

/** Changes the one inventory item a reservation takes, and how many of it. */
@Component({
  selector: 'app-reservation-items',
  imports: [
    FormsModule,
    RouterLink,
    ButtonModule,
    CardModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    ProgressSpinnerModule,
    ToastModule,
    StockCard,
  ],
  templateUrl: './reservation-items.html',
  styleUrl: './reservation-items.css',
})
export class ReservationItems {
  private readonly api = inject(BookingApi);
  private readonly messages = inject(MessageService);
  private readonly router = inject(Router);

  protected readonly id = Number(inject(ActivatedRoute).snapshot.paramMap.get('id'));

  protected readonly booking = signal<Booking | null>(null);
  protected readonly stock = signal<StockItem[]>([]);
  protected readonly choice = signal<ItemChoice | null>(null);
  protected readonly search = signal('');
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);

  protected readonly shown = computed(() => {
    const needle = this.search().trim().toLowerCase();

    return this.stock().filter((item) => !needle || item.name.toLowerCase().includes(needle));
  });

  protected readonly changed = computed(() => {
    const before = this.booking()?.item;
    const after = this.choice();

    return (
      (before?.inventoryId ?? null) !== (after?.inventoryId ?? null) ||
      (before?.quantity ?? 0) !== (after?.quantity ?? 0)
    );
  });

  constructor() {
    this.api
      .get(this.id)
      .pipe(
        tap((booking) => {
          this.booking.set(booking);
          this.choice.set(
            booking.item && { inventoryId: booking.item.inventoryId, quantity: booking.item.quantity },
          );
        }),
        switchMap((booking) => this.api.listStock(booking.venueId)),
      )
      .subscribe({
        next: (stock) => {
          this.stock.set(stock);
          this.loading.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.loading.set(false);
          this.messages.add({
            severity: 'error',
            summary: 'Could not load items',
            detail: describeBookingError(error),
          });
        },
      });
  }

  protected quantityOf(item: StockItem): number {
    const choice = this.choice();

    return choice?.inventoryId === item.id ? choice.quantity : 0;
  }

  protected heldOf(item: StockItem): number {
    const held = this.booking()?.item;

    return held?.inventoryId === item.id ? held.quantity : 0;
  }

  protected setQuantity(item: StockItem, quantity: number): void {
    if (quantity > 0) {
      this.choice.set({ inventoryId: item.id, quantity });
    } else if (this.choice()?.inventoryId === item.id) {
      this.choice.set(null);
    }
  }

  protected save(): void {
    if (this.saving()) {
      return;
    }

    this.saving.set(true);

    this.api.setItem(this.id, this.choice()).subscribe({
      next: () => {
        this.saving.set(false);
        this.router.navigate(['/reservations', this.id], {
          state: { notice: { summary: 'Items updated' } satisfies Notice },
        });
      },
      error: (error: HttpErrorResponse) => {
        this.saving.set(false);
        this.messages.add({
          severity: 'error',
          summary: 'Could not update items',
          detail: describeBookingError(error),
        });
      },
    });
  }
}
