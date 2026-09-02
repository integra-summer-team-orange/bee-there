import { Injectable, signal } from '@angular/core';
import { InventoryItem } from './inventory-model';

const DUMMY_DATA: InventoryItem[] = [
  { id: '1', venue_id: 'cluj-arena', name: 'Basketballs', total_quantity: 60, available_quantity: 20 },
  { id: '2', venue_id: 'cluj-arena', name: 'Tennis Rackets', total_quantity: 12, available_quantity: 10 },
  { id: '3', venue_id: 'cluj-arena', name: 'Footballs', total_quantity: 120, available_quantity: 0 },
  { id: '4', venue_id: 'cluj-arena', name: 'Badminton Rackets', total_quantity: 8, available_quantity: 1 },
  { id: '5', venue_id: 'cluj-arena', name: 'Volleyballs', total_quantity: 30, available_quantity: 15 },
  { id: '6', venue_id: 'cluj-arena', name: 'Table Tennis Paddles', total_quantity: 16, available_quantity: 14 },
  { id: '7', venue_id: 'cluj-arena', name: 'Yoga Mats', total_quantity: 25, available_quantity: 25 },
];

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly _items = signal<InventoryItem[]>(DUMMY_DATA);
  readonly items = this._items.asReadonly();

  getById(id: string): InventoryItem | undefined {
    return this._items().find(item => item.id === id);
  }

  addItem(name: string, total: number, available: number) {
    const newItem: InventoryItem = {
      id: crypto.randomUUID(),
      venue_id: 'cluj-arena',
      name,
      total_quantity: total,
      available_quantity: available,
    };
    this._items.update(list => [newItem, ...list]);
  }

  updateItem(updatedItem: InventoryItem) {
    this._items.update(list =>
      list.map(item => (item.id === updatedItem.id ? updatedItem : item))
    );
  }

  deleteItem(id: string) {
    this._items.update(list => list.filter(item => item.id !== id));
  }
}
