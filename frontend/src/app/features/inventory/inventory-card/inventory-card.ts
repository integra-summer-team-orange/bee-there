import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from 'primeng/card';
import { Button } from 'primeng/button';
import { ProgressBarModule } from 'primeng/progressbar';

export interface InventoryItem {
  id: string;
  venue_id: string;
  name: string;
  total_quantity: number;
  available_quantity: number;
}

@Component({
  selector: 'inventory-card',
  standalone: true,
  imports: [CommonModule, Card, Button, ProgressBarModule],
  templateUrl: './inventory-card.html',
  styleUrl: './inventory-card.css',
})
export class InventoryCard {
  @Input({ required: true }) item!: InventoryItem;
  @Output() edit = new EventEmitter<InventoryItem>();
  @Output() delete = new EventEmitter<string>();

  get percentage(): number {
    if (!this.item?.total_quantity) return 0;
    return (this.item.available_quantity / this.item.total_quantity) * 100;
  }

  get statusColorClass(): string {
    if (this.percentage > 50) return 'bg-success';
    if (this.percentage > 20) return 'bg-warning';
    return 'bg-danger';
  }
}
