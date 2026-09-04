import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { InventoryModel } from '../inventory-service/inventory-model';

@Component({
  selector: 'inventory-overlays',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule
  ],
  templateUrl: './inventory-overlays.html',
  styleUrl: './inventory-overlays.css'
})
export class InventoryOverlays {
  @Input() visible = false;
  @Input() mode: 'create' | 'edit' | 'delete' = 'create';
  @Input() item: Partial<InventoryModel> = {};

  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() confirm = new EventEmitter<Partial<InventoryModel>>();
  @Output() delete = new EventEmitter<string>();

  get headerText(): string {
    switch (this.mode) {
      case 'create': return 'Add New Item';
      case 'edit': return 'Edit Item';
      case 'delete': return 'Delete Item';
      default: return '';
    }
  }

  onHide() {
    this.visibleChange.emit(false);
  }

  // Returns true when availableQuantity exceeds totalQuantity
  get isQuantityValid(): boolean {
    if (this.mode === 'delete') return false;
    const total = this.item.totalQuantity ?? 0;
    const available = this.item.availableQuantity ?? 0;
    return available <= total;
  }

  onConfirm() {
    if (this.mode === 'delete') {
      if (this.item.id) {
        this.delete.emit(this.item.id);
      }
    } else {
      if (!this.isQuantityValid) {
        return;
      }
      this.confirm.emit(this.item);
    }
    this.onHide();
  }
}
