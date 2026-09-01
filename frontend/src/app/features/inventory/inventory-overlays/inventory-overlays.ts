import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { InventoryItem } from '../inventory-card/inventory-card';

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
  @Input() item: Partial<InventoryItem> = {};

  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() confirm = new EventEmitter<Partial<InventoryItem>>();
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

  onConfirm() {
    if (this.mode === 'delete') {
      if (this.item.id) {
        this.delete.emit(this.item.id);
      }
    } else {
      this.confirm.emit(this.item);
    }
    this.onHide();
  }
}
