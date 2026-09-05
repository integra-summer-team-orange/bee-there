import { Injectable, signal, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { InventoryService as InventoryApiService, InventoryDto } from '../../../../api/generated';
import {MOCK_INVENTORY_ITEMS} from './inventory.mock';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private inventoryApi = inject(InventoryApiService);

  // TODO: Integrate a signal passed from the Venue slice
  protected selectedVenue = 1;

  private readonly _items = signal<InventoryDto[]>([]);
  readonly items = this._items.asReadonly();

  private readonly _loading = signal<boolean>(false);
  readonly loading = this._loading.asReadonly();

  protected pageNumber = 0;
  protected pageSize = 10;

  // Loading Items from the backend
  async loadItems(page = this.pageNumber, size = this.pageSize) {
    this._loading.set(true);
    try {
      const response = await firstValueFrom(this.inventoryApi.getAllInventoryItems(page, size));
      const items = response.content || [];

      // seed the database should it be empty upon initial load
      if (items.length === 0 && (response.totalElements === 0 || response.totalElements === undefined)) {
        await this.seedDatabase();
        return;
      }

      this._items.set(items);
    } finally {
      this._loading.set(false);
    }
  }

  // function that populates the database with mock items for testing
  private async seedDatabase() {
    for (const item of MOCK_INVENTORY_ITEMS) {
      await firstValueFrom(this.inventoryApi.createInventoryItem(item));
    }
    // Reload items from backend now that DB is populated
    const reloaded = await firstValueFrom(
      this.inventoryApi.getAllInventoryItems(this.pageNumber, this.pageSize)
    );
    this._items.set(reloaded.content || []);
  }

  async addItem(name: string, total: number, available: number) {
    const dto: InventoryDto = {
      venueId: this.selectedVenue,
      name,
      totalQuantity: total,
      availableQuantity: available,
    };
    const newItem = await firstValueFrom(this.inventoryApi.createInventoryItem(dto));
    this._items.update(list => [newItem, ...list]);
  }

  async updateItem(updatedItem: InventoryDto) {
    const dto: InventoryDto = {
      id: Number(updatedItem.id),
      venueId: Number(updatedItem.venueId),
      name: updatedItem.name,
      totalQuantity: updatedItem.totalQuantity,
      availableQuantity: updatedItem.availableQuantity
    }
    const response = await firstValueFrom(this.inventoryApi.updateInventoryItem(dto.id!, dto));
    this._items.update(list =>
      list.map(item => (item.id === updatedItem.id ? response : item))
    );
  }

  async deleteItem(id: number) {
    await firstValueFrom(this.inventoryApi.deleteInventoryItem(id));
    this._items.update(list => list.filter(item => item.id !== id));
  }

  getById(id: number): InventoryDto | undefined {
    return this._items().find(item => item.id === id);
  }
}
