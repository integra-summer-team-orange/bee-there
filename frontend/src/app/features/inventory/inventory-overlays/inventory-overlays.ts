import { Component, EventEmitter, input, model, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { InventoryDto } from '../../../../api/generated';

@Component({
  selector: 'inventory-overlays',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DialogModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
  ],
  templateUrl: './inventory-overlays.html',
  styleUrl: './inventory-overlays.css',
})
export class InventoryOverlays {
  visible = model(false);
  mode = input<'create' | 'edit' | 'delete'>('create');
  item = model<Partial<InventoryDto>>({});

  confirm = output<Partial<InventoryDto>>();
  delete = output<number>();

  get headerText(): string {
    switch (this.mode()) {
      case 'create':
        return 'Add New Item';
      case 'edit':
        return 'Edit Item';
      case 'delete':
        return 'Delete Item';
      default:
        return '';
    }
  }

  get isQuantityInvalid(): boolean {
    if (this.mode() === 'delete') return false;

    const total = this.item().totalQuantity ?? 0;
    const available = this.item().availableQuantity ?? 0;

    return total < 0 || available < 0 || available > total;
  }

  updateName(value: string) {
    this.item.update(i => ({ ...i, name: value }));
  }

  updateTotal(value: number) {
    this.item.update(i => ({ ...i, totalQuantity: value }));
  }

  updateAvailable(value: number) {
    this.item.update(i => ({ ...i, availableQuantity: value }));
  }

  onHide() {
    this.visible.set(false);
  }

  onConfirm() {
    if (this.mode() === 'delete') {
      if (this.item().id !== undefined) {
        this.delete.emit(this.item().id!);
      }
      this.onHide();
      return;
    }

    if (this.isQuantityInvalid || !this.item().name?.trim()) {
      return;
    }

    this.confirm.emit(this.item());
    this.onHide();
  }
}
