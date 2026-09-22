// inventory-wrapper-service.ts
import { Injectable, computed, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  InventoryService as InventoryApiService,
  InventoryDto,
  VenueDto,
  VenuesService,
} from '../../../../api/generated';

@Injectable({
  providedIn: 'root',
})
export class InventoryWrapperService {
  private readonly inventoryApi = inject(InventoryApiService);
  private readonly venuesApi = inject(VenuesService);

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
      console.error('Failed to fetch venue details', e);
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
    } finally {
      this._loading.set(false);
    }
  }

  async addItem(name: string, total: number, available: number) {
    const venueId = this.selectedVenueId();
    if (!venueId) {
      throw new Error('Cannot add item: No venue is currently selected.');
    }

    const dto: InventoryDto = {
      venueId,
      name,
      totalQuantity: total,
      availableQuantity: available,
    };
    const newItem = await firstValueFrom(this.inventoryApi.createInventoryItem(dto));
    this._items.update((list) => [newItem, ...list]);
  }

  async updateItem(updatedItem: InventoryDto) {
    const dto: InventoryDto = {
      id: Number(updatedItem.id),
      venueId: Number(updatedItem.venueId ?? this.selectedVenueId()),
      name: updatedItem.name,
      totalQuantity: updatedItem.totalQuantity,
      availableQuantity: updatedItem.availableQuantity,
    };
    const response = await firstValueFrom(this.inventoryApi.updateInventoryItem(dto.id!, dto));
    this._items.update((list) =>
      list.map((item) => (item.id === updatedItem.id ? response : item))
    );
  }

  async deleteItem(id: number) {
    await firstValueFrom(this.inventoryApi.deleteInventoryItem(id));
    this._items.update((list) => list.filter((item) => item.id !== id));
  }

  getById(id: number): InventoryDto | undefined {
    return this._items().find((item) => item.id === id);
  }
}
