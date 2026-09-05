import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from 'primeng/card';
import { Button } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';
import { InventoryDto } from '../../../../api/generated';

@Component({
  selector: 'inventory-card',
  standalone: true,
  imports: [CommonModule, Card, Button, ProgressBarModule],
  templateUrl: './inventory-card.html',
  styleUrl: './inventory-card.css',
})
export class InventoryCard {
  @Input({ required: true }) item!: InventoryDto;
  @Output() edit = new EventEmitter<InventoryDto>();
  @Output() delete = new EventEmitter<number>();

  get percentage(): number {
    if (!this.item?.totalQuantity) return 0;
    return (this.item.availableQuantity / this.item.totalQuantity) * 100;
  }

  get statusColorClass(): string {
    if (this.percentage > 50) return 'bg-success';
    if (this.percentage > 20) return 'bg-warning';
    return 'bg-danger';
  }
}
