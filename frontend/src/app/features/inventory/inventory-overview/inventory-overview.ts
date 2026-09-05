import {Component, computed, inject, OnInit, signal} from '@angular/core';
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
import { InventoryDto } from '../../../../api/generated';

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
export class InventoryOverview implements OnInit {
  private inventoryService = inject(InventoryService);

  // Master state
  readonly items = this.inventoryService.items;
  readonly searchQuery = signal<string>('');
  readonly loading = this.inventoryService.loading;

  // Pagination state
  first = signal(0);
  rows = signal(6);

  // Overlay state
  overlayVisible = signal(false);
  overlayMode = signal<'create' | 'edit' | 'delete'>('create');
  selectedItem = signal<Partial<InventoryDto>>({});

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

  ngOnInit() {
    this.inventoryService.loadItems();
  }

  onPageChange(event: PaginatorState) {
    this.first.set(event.first ?? 0);
    this.rows.set(event.rows ?? 6);
  }

  // --- CRUD Operations ---

  openCreate() {
    this.selectedItem.set({ name: '', totalQuantity: 0, availableQuantity: 0 });
    this.overlayMode.set('create');
    this.overlayVisible.set(true);
  }

  openEdit(item: InventoryDto) {
    this.selectedItem.set({ ...item });
    this.overlayMode.set('edit');
    this.overlayVisible.set(true);
  }

  openDelete(id: number) {
    const item = this.inventoryService.getById(id);
    if (item) {
      this.selectedItem.set({ ...item });
      this.overlayMode.set('delete');
      this.overlayVisible.set(true);
    }
  }

  async handleConfirm(item: Partial<InventoryDto>) {
    if (this.overlayMode() === 'create') {
      await this.inventoryService.addItem(item.name || '', item.totalQuantity || 0, item.availableQuantity || 0);
    } else if (this.overlayMode() === 'edit') {
      await this.inventoryService.updateItem(item as InventoryDto);
    }
    this.overlayVisible.set(false);
  }

  async deleteItem(id: number) {
    await this.inventoryService.deleteItem(id);
    this.overlayVisible.set(false);
  }
}
