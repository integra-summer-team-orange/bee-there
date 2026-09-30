import { Component, computed, input, output } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { CardModule } from 'primeng/card';

import { ResourceOption, readableResourceType } from '../reservation-models';

@Component({
  selector: 'app-resource-card',
  imports: [DecimalPipe, CardModule],
  templateUrl: './resource-card.html',
  styleUrl: './resource-card.css',
})
export class ResourceCard {
  readonly resource = input.required<ResourceOption>();

  readonly selected = input<boolean>(false);

  readonly choose = output<ResourceOption>();

  protected readonly readableType = readableResourceType;

  protected readonly cardClass = computed(() =>
    [
      'h-full wrap-anywhere border shadow-md transition-colors',
      this.selected()
        ? 'border-transparent'
        : 'border-outline-variant hover:border-general-highlight',
    ].join(' '),
  );

  protected readonly cardStyle = computed(() =>
    this.selected()
      ? {
          backgroundColor: 'var(--color-selected-container)',
          borderColor: 'var(--color-selected-outline)',
        }
      : {},
  );
}
