import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService } from 'primeng/api';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ToastModule } from 'primeng/toast';

import { VenueDto, VenuesService } from '../../../../api/generated';
import { Session } from '../../../core/services/session';
import { VenueCard } from '../venue-card/venue-card';
import { describeVenueError } from '../venue-error';

const PAGE_SIZE = 6;

/** How long to wait after the last keystroke before asking the backend again. */
const SEARCH_DEBOUNCE_MS = 300;

/** Lists the venues the signed-in user manages and lets them be created, opened or deleted. */
@Component({
  selector: 'app-venue-overview',
  imports: [
    FormsModule,
    RouterLink,
    ButtonModule,
    DialogModule,
    IconFieldModule,
    InputIconModule,
    InputTextModule,
    PaginatorModule,
    ProgressSpinnerModule,
    ToastModule,
    VenueCard,
  ],
  templateUrl: './venue-overview.html',
  styleUrl: './venue-overview.css',
})
export class VenueOverview {
  private readonly venues = inject(VenuesService);
  private readonly messages = inject(MessageService);
  private readonly session = inject(Session);

  protected readonly loading = signal(false);
  protected readonly deleting = signal(false);
  protected readonly page = signal(0);
  protected readonly totalRecords = signal(0);
  protected readonly search = signal('');
  protected readonly venueToDelete = signal<VenueDto | null>(null);

  protected readonly results = signal<VenueDto[]>([]);

  protected readonly pageSize = PAGE_SIZE;
  protected readonly first = computed(() => this.page() * PAGE_SIZE);

  private searchTimer?: ReturnType<typeof setTimeout>;

  constructor() {
    this.load();
  }

  /** Whether the signed-in user may edit or delete the given venue. */
  protected manageable(venue: VenueDto): boolean {
    return this.session.canManage(venue.managedBy);
  }

  /** Loads the current page of venues managed by the signed-in user, narrowed by the search term. */
  protected load(): void {
    this.loading.set(true);

    this.venues.getMine(this.page(), PAGE_SIZE, this.search().trim() || undefined).subscribe({
      next: (page) => {
        this.results.set(page.content ?? []);
        this.totalRecords.set(page.totalElements ?? 0);
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messages.add({
          severity: 'error',
          summary: 'Could not load venues',
          detail: describeVenueError(error),
        });
      },
    });
  }

  /** Searches again from the first page once the user stops typing. */
  protected onSearchChange(term: string): void {
    this.search.set(term);
    clearTimeout(this.searchTimer);
    this.searchTimer = setTimeout(() => {
      this.page.set(0);
      this.load();
    }, SEARCH_DEBOUNCE_MS);
  }

  /** Moves to another page of results. */
  protected onPageChange(event: PaginatorState): void {
    this.page.set(event.page ?? 0);
    this.load();
  }

  /** Opens the delete confirmation for a venue. */
  protected askToDelete(venue: VenueDto): void {
    this.venueToDelete.set(venue);
  }

  /** Closes the delete confirmation without deleting anything. */
  protected cancelDelete(): void {
    this.venueToDelete.set(null);
  }

  /** Deletes the venue currently held in the confirmation dialog. */
  protected confirmDelete(): void {
    const venue = this.venueToDelete();
    if (!venue?.id || this.deleting()) {
      return;
    }

    this.deleting.set(true);

    this.venues.deleteVenue(venue.id).subscribe({
      next: () => {
        this.deleting.set(false);
        this.venueToDelete.set(null);
        this.messages.add({
          severity: 'success',
          summary: 'Venue deleted',
          detail: `${venue.name} has been removed.`,
        });
        this.afterDelete();
      },
      error: (error: HttpErrorResponse) => {
        this.deleting.set(false);
        this.venueToDelete.set(null);
        this.messages.add({
          severity: 'error',
          summary: 'Could not delete venue',
          detail: describeVenueError(error),
        });
      },
    });
  }

  /** Steps back a page when the last venue on it was just deleted, then reloads. */
  private afterDelete(): void {
    if (this.results().length === 1 && this.page() > 0) {
      this.page.update((page) => page - 1);
    }

    this.load();
  }
}
