import { Component, computed, inject, signal } from '@angular/core';
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

import { InventoryCard } from '../inventory-card/inventory-card';
import { InventoryOverlays } from '../inventory-overlays/inventory-overlays';
import { InventoryService } from '../inventory-service/inventory-service';
import { InventoryItem } from '../inventory-service/inventory-model';

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
  private inventoryService = inject(InventoryService);

  // Master state
  readonly items = this.inventoryService.items;
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
    const item = this.inventoryService.getById(id);
    if (item) {
      this.selectedItem = { ...item };
      this.overlayMode = 'delete';
      this.overlayVisible = true;
    }
  }

  handleConfirm(item: Partial<InventoryItem>) {
    if (this.overlayMode === 'create') {
      this.inventoryService.addItem(item.name || '', item.total_quantity || 0, item.available_quantity || 0);
    } else if (this.overlayMode === 'edit') {
      this.inventoryService.updateItem(item as InventoryItem);
    }
  }

  // DELETE
  deleteItem(id: string) {
    this.inventoryService.deleteItem(id);
  }
}
