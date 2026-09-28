import { afterNextRender, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

export interface Notice {
  summary: string;
  detail?: string;
}

/**
 * Shows the success message the previous screen passed in the navigation state. Adding it before
 * navigating loses it, because the old screen's toast is destroyed and the new one has not rendered.
 */
export function showNavigationNotice(): void {
  const messages = inject(MessageService);
  const notice = inject(Router).currentNavigation()?.extras.state?.['notice'] as Notice | undefined;

  if (notice) {
    afterNextRender(() => messages.add({ severity: 'success', ...notice }));
  }
}
