import { Injectable, signal } from '@angular/core';
import { InventoryModel } from './inventory-model';


const DUMMY_DATA: InventoryModel[] = [
  { id: '1', venueId: 'cluj-arena', name: 'Basketballs', totalQuantity: 60, availableQuantity: 20 },
  { id: '2', venueId: 'cluj-arena', name: 'Tennis Rackets', totalQuantity: 12, availableQuantity: 10 },
  { id: '3', venueId: 'cluj-arena', name: 'Footballs', totalQuantity: 120, availableQuantity: 0 },
  { id: '4', venueId: 'cluj-arena', name: 'Badminton Rackets', totalQuantity: 8, availableQuantity: 1 },
  { id: '5', venueId: 'cluj-arena', name: 'Volleyballs', totalQuantity: 30, availableQuantity: 15 },
  { id: '6', venueId: 'cluj-arena', name: 'Table Tennis Paddles', totalQuantity: 16, availableQuantity: 14 },
  { id: '7', venueId: 'cluj-arena', name: 'Yoga Mats', totalQuantity: 25, availableQuantity: 25 },
];

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly _items = signal<InventoryModel[]>(DUMMY_DATA);
  readonly items = this._items.asReadonly();

  getById(id: string): InventoryModel | undefined {
    return this._items().find(item => item.id === id);
  }

  addItem(name: string, total: number, available: number) {
    const newItem: InventoryModel = {
      id: crypto.randomUUID(),
      venueId: 'cluj-arena',
      name,
      totalQuantity: total,
      availableQuantity: available,
    };
    this._items.update(list => [newItem, ...list]);
  }

  updateItem(updatedItem: InventoryModel) {
    this._items.update(list =>
      list.map(item => (item.id === updatedItem.id ? updatedItem : item))
    );
  }

  deleteItem(id: string) {
    this._items.update(list => list.filter(item => item.id !== id));
  }
}
