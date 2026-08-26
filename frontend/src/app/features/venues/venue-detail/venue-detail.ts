import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MessageService } from 'primeng/api';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TextareaModule } from 'primeng/textarea';
import { MessageModule } from 'primeng/message';
import { ToastModule } from 'primeng/toast';
import { TooltipModule } from 'primeng/tooltip';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import {
  InventoryDto,
  InventoryService,
  ResourceDto,
  ResourcesService,
  VenueDto,
  VenuesService,
} from '../../../../api/generated';
import { Session } from '../../../core/services/session';
import { describeVenueError } from '../venue-error';

/** Which of the three screens from the design this component is currently showing. */
export type VenueDetailMode = 'view' | 'edit' | 'create';

/** How many resource and inventory rows to pull when summarising a venue. */
const RELATED_PAGE_SIZE = 200;

/** The venue view, edit and create screens, which share one layout and differ only in what is editable. */
@Component({
  selector: 'app-venue-detail',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    RouterLink,
    ButtonModule,
    CardModule,
    InputTextModule,
    MessageModule,
    ProgressSpinnerModule,
    TextareaModule,
    ToastModule,
    TooltipModule,
  ],
  templateUrl: './venue-detail.html',
  styleUrl: './venue-detail.css',
})
export class VenueDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly venues = inject(VenuesService);
  private readonly resources = inject(ResourcesService);
  private readonly inventory = inject(InventoryService);
  private readonly messages = inject(MessageService);
  private readonly session = inject(Session);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly mode = signal<VenueDetailMode>('view');
  protected readonly venue = signal<VenueDto | null>(null);
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly venueResources = signal<ResourceDto[]>([]);
  protected readonly venueInventory = signal<InventoryDto[]>([]);

  protected readonly form = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(150)]],
    description: ['', [Validators.maxLength(255)]],
    address: ['', [Validators.required, Validators.maxLength(255)]],
  });

  /** Whether the signed-in user may edit this venue. */
  protected readonly manageable = computed(() => this.session.canManage(this.venue()?.managedBy));

  /**
   * True while the form is actually editable.
   *
   * The edit route can be opened directly, so reaching it is not enough: a venue someone else manages stays
   * read-only here, which matches the refusal the backend would return anyway.
   */
  protected readonly editing = computed(
    () => this.mode() === 'create' || (this.mode() === 'edit' && this.manageable()),
  );

  /** True when the edit screen was opened for a venue the signed-in user does not manage. */
  protected readonly editingRefused = computed(
    () => this.mode() === 'edit' && this.venue() !== null && !this.manageable(),
  );

  /** Share of the venue's stock currently checked out, as a whole percentage. */
  protected readonly bookedPercentage = computed(() => {
    const items = this.venueInventory();
    const total = items.reduce((sum, item) => sum + (item.totalQuantity ?? 0), 0);

    if (total === 0) {
      return 0;
    }

    const available = items.reduce((sum, item) => sum + (item.availableQuantity ?? 0), 0);

    return Math.round(((total - available) / total) * 100);
  });

  constructor() {
    const mode = (this.route.snapshot.data['mode'] as VenueDetailMode | undefined) ?? 'view';
    this.mode.set(mode);

    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (mode === 'create' || !id) {
      return;
    }

    this.load(id);
    this.loadRelated(id);
  }

  /** Switches from viewing to editing the venue currently on screen. */
  protected startEditing(): void {
    const id = this.venue()?.id;

    if (id) {
      this.router.navigate(['/venues', id, 'edit']);
    }
  }

  /** Leaves the form without saving. */
  protected cancel(): void {
    const id = this.venue()?.id;

    if (this.mode() === 'create' || !id) {
      this.router.navigate(['/venues']);
      return;
    }

    this.router.navigate(['/venues', id]);
  }

  /** Creates or updates the venue, then returns to its detail screen. */
  protected save(): void {
    if (this.saving() || !this.editing()) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);

    const creating = this.mode() === 'create';
    const payload = this.form.getRawValue();
    const id = this.venue()?.id;
    const request = creating || !id ? this.venues.create(payload) : this.venues.update(id, payload);

    request.subscribe({
      next: (saved) => {
        this.saving.set(false);
        this.messages.add({
          severity: 'success',
          summary: creating ? 'Venue created' : 'Venue updated',
          detail: saved.name + ' has been saved.',
        });
        this.router.navigate(['/venues', saved.id]);
      },
      error: (error: HttpErrorResponse) => {
        this.saving.set(false);
        this.messages.add({
          severity: 'error',
          summary: 'Could not save venue',
          detail: describeVenueError(error),
        });
      },
    });
  }

  private load(id: number): void {
    this.loading.set(true);

    this.venues.getById(id).subscribe({
      next: (venue) => {
        this.venue.set(venue);
        this.form.patchValue({
          name: venue.name ?? '',
          description: venue.description ?? '',
          address: venue.address ?? '',
        });
        this.loading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loading.set(false);
        this.messages.add({
          severity: 'error',
          summary: 'Could not load venue',
          detail: describeVenueError(error),
        });
      },
    });
  }

  /**
   * Loads the resources and inventory belonging to this venue.
   *
   * Neither endpoint can filter by venue yet, so a generous page is fetched and narrowed here. A failure is
   * swallowed on purpose: these summary cards are secondary and must not take the whole screen down with them.
   */
  private loadRelated(id: number): void {
    forkJoin({
      resources: this.resources.getAll2(0, RELATED_PAGE_SIZE).pipe(catchError(() => of(null))),
      inventory: this.inventory.getAll4(0, RELATED_PAGE_SIZE).pipe(catchError(() => of([]))),
    }).subscribe(({ resources, inventory }) => {
      const all = (resources?.content ?? []) as ResourceDto[];

      this.venueResources.set(all.filter((resource) => resource.venueId === id));
      this.venueInventory.set(inventory.filter((item) => item.venueId === id));
    });
  }
}
