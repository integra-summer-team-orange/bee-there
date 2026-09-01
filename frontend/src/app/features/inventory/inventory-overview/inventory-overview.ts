import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { ToastModule } from 'primeng/toast';

import { InventoryCard, InventoryItem } from '../inventory-card/inventory-card';
import { InventoryOverlays } from '../inventory-overlays/inventory-overlays';

const DUMMY_DATA: InventoryItem[] = [
  { id: '1', venue_id: 'cluj-arena', name: 'Basketballs', total_quantity: 60, available_quantity: 20 },
  { id: '2', venue_id: 'cluj-arena', name: 'Tennis Rackets', total_quantity: 12, available_quantity: 10 },
  { id: '3', venue_id: 'cluj-arena', name: 'Footballs', total_quantity: 120, available_quantity: 0 },
  { id: '4', venue_id: 'cluj-arena', name: 'Badminton Rackets', total_quantity: 8, available_quantity: 1 },
  { id: '5', venue_id: 'cluj-arena', name: 'Volleyballs', total_quantity: 30, available_quantity: 15 },
  { id: '6', venue_id: 'cluj-arena', name: 'Table Tennis Paddles', total_quantity: 16, available_quantity: 14 },
  { id: '7', venue_id: 'cluj-arena', name: 'Yoga Mats', total_quantity: 25, available_quantity: 25 },
];

@Component({
  selector: 'app-inventory-overview',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    ButtonModule,
    DialogModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    PaginatorModule,
    ToastModule,
    InventoryCard,
    InventoryOverlays
  ],
  templateUrl: './inventory-overview.html',
  styleUrl: './inventory-overview.css',
})
export class InventoryOverview {
  // Master state
  readonly items = signal<InventoryItem[]>(DUMMY_DATA);
  readonly searchQuery = signal<string>('');

  // Pagination state
  first = signal(0);
  rows = signal(6);

  // Overlay state
  overlayVisible = false;
  overlayMode: 'create' | 'edit' | 'delete' = 'create';
  selectedItem: Partial<InventoryItem> = {};

  // Filtered & Paginated items
  readonly filteredItems = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    return this.items().filter(item => item.name.toLowerCase().includes(query));
  });

  readonly paginatedItems = computed(() => {
    const items = this.filteredItems();
    const start = this.first();
    // Adjust start if it's out of bounds after filtering
    const actualStart = start >= items.length ? 0 : start;
    return items.slice(actualStart, actualStart + this.rows());
  });

  onPageChange(event: PaginatorState) {
    this.first.set(event.first ?? 0);
    this.rows.set(event.rows ?? 6);
  }

  // --- CRUD Operations ---

  openCreate() {
    this.selectedItem = { name: '', total_quantity: 0, available_quantity: 0 };
    this.overlayMode = 'create';
    this.overlayVisible = true;
  }

  openEdit(item: InventoryItem) {
    this.selectedItem = { ...item };
    this.overlayMode = 'edit';
    this.overlayVisible = true;
  }

  openDelete(id: string) {
    const item = this.items().find(i => i.id === id);
    if (item) {
      this.selectedItem = { ...item };
      this.overlayMode = 'delete';
      this.overlayVisible = true;
    }
  }

  handleConfirm(item: Partial<InventoryItem>) {
    if (this.overlayMode === 'create') {
      this.addItem(item.name || '', item.total_quantity || 0, item.available_quantity || 0);
    } else if (this.overlayMode === 'edit') {
      this.updateItem(item as InventoryItem);
    }
  }

  // CREATE
  addItem(name: string, total: number, available: number) {
    const newItem: InventoryItem = {
      id: crypto.randomUUID(),
      venue_id: 'cluj-arena',
      name,
      total_quantity: total,
      available_quantity: available
    };
    this.items.update(list => [newItem, ...list]);
  }

  // UPDATE
  updateItem(updatedItem: InventoryItem) {
    this.items.update(list =>
      list.map(item => (item.id === updatedItem.id ? updatedItem : item))
    );
  }

  // DELETE
  deleteItem(id: string) {
    this.items.update(list => list.filter(item => item.id !== id));
  }
}
