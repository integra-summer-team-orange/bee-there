// inventory-wrapper-service.ts
import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import {
  InventoryService as InventoryApiService,
  InventoryDto,
  VenueDto,
  VenuesService,
} from '../../../../api/generated';
import { describeInventoryError } from '../inventory-error';

@Injectable({
  providedIn: 'root',
})
export class InventoryWrapperService {
  private readonly inventoryApi = inject(InventoryApiService);
  private readonly venuesApi = inject(VenuesService);
  private readonly messages = inject(MessageService);

  private readonly _currentVenue = signal<VenueDto | null>(null);
  private readonly _venueId = signal<number | null>(null);

  readonly selectedVenueId = computed(() => this._venueId() ?? this._currentVenue()?.id ?? null);
  readonly selectedVenueName = computed(() => this._currentVenue()?.name ?? '');

  private readonly _items = signal<InventoryDto[]>([]);
  readonly items = this._items.asReadonly();

  private readonly _loading = signal<boolean>(false);
  readonly loading = this._loading.asReadonly();

  protected pageNumber = 0;
  protected pageSize = 200;

  /** Set venue directly when full object is already loaded */
  setVenue(venue: VenueDto | null): void {
    this._currentVenue.set(venue);
    if (venue) {
      this._venueId.set(venue.id!);
    }
  }

  /** Set only the ID, e.g. from a route parameter */
  setVenueId(id: number | null): void {
    this._venueId.set(id);
    // If we have a current venue but it's different from the new ID, clear it
    if (this._currentVenue()?.id !== id) {
      this._currentVenue.set(null);
    }
    if (id && !this._currentVenue()) {
      this.fetchVenue(id);
    }
  }

  private async fetchVenue(id: number) {
    try {
      const venue = await firstValueFrom(this.venuesApi.getVenueById(id));
      this._currentVenue.set(venue);
    } catch (e) {
      this.messages.add({
        severity: 'error',
        summary: 'Could not load venue',
        detail: describeInventoryError(e as HttpErrorResponse),
      });
    }
  }

  async loadItems(page = this.pageNumber, size = this.pageSize) {
    const venueId = this.selectedVenueId();

    if (!venueId) {
      this._items.set([]);
      return;
    }

    this._loading.set(true);
    try {
      const response = await firstValueFrom(this.venuesApi.getInventoryByVenue(venueId, page, size));
      const items = response.content || [];
      this._items.set(items);
    } catch (e) {
      this.messages.add({
        severity: 'error',
        summary: 'Could not load inventory',
        detail: describeInventoryError(e as HttpErrorResponse),
      });
    } finally {
      this._loading.set(false);
    }
  }

  async addItem(name: string, total: number, available: number) {
    const venueId = this.selectedVenueId();
    if (!venueId) {
      this.messages.add({
        severity: 'error',
        summary: 'Cannot add item',
        detail: 'No venue is currently selected.',
      });
      return;
    }

    const dto: InventoryDto = {
      venueId,
      name,
      totalQuantity: total,
      availableQuantity: available,
    };

    try {
      const newItem = await firstValueFrom(this.inventoryApi.createInventoryItem(dto));
      this._items.update((list) => [newItem, ...list]);
      this.messages.add({
        severity: 'success',
        summary: 'Item added',
        detail: `${name} has been added to the inventory.`,
      });
    } catch (e) {
      this.messages.add({
        severity: 'error',
        summary: 'Could not add item',
        detail: describeInventoryError(e as HttpErrorResponse),
      });
    }
  }

  async updateItem(updatedItem: InventoryDto) {
    const dto: InventoryDto = {
      id: Number(updatedItem.id),
      venueId: Number(updatedItem.venueId ?? this.selectedVenueId()),
      name: updatedItem.name,
      totalQuantity: updatedItem.totalQuantity,
      availableQuantity: updatedItem.availableQuantity,
    };

    try {
      const response = await firstValueFrom(this.inventoryApi.updateInventoryItem(dto.id!, dto));
      this._items.update((list) =>
        list.map((item) => (item.id === updatedItem.id ? response : item))
      );
      this.messages.add({
        severity: 'success',
        summary: 'Item updated',
        detail: `${updatedItem.name} has been updated.`,
      });
    } catch (e) {
      this.messages.add({
        severity: 'error',
        summary: 'Could not update item',
        detail: describeInventoryError(e as HttpErrorResponse),
      });
    }
  }

  async deleteItem(id: number) {
    const item = this.getById(id);
    try {
      await firstValueFrom(this.inventoryApi.deleteInventoryItem(id));
      this._items.update((list) => list.filter((item) => item.id !== id));
      this.messages.add({
        severity: 'success',
        summary: 'Item deleted',
        detail: item ? `${item.name} has been removed.` : 'Item has been removed.',
      });
    } catch (e) {
      this.messages.add({
        severity: 'error',
        summary: 'Could not delete item',
        detail: describeInventoryError(e as HttpErrorResponse),
      });
    }
  }

  getById(id: number): InventoryDto | undefined {
    return this._items().find((item) => item.id === id);
  }
}
