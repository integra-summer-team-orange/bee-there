import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DatePickerModule } from 'primeng/datepicker';
import { MessageModule } from 'primeng/message';
import { MessageService } from 'primeng/api';
import { ProgressBarModule } from 'primeng/progressbar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { SelectModule } from 'primeng/select';
import { AvatarModule } from 'primeng/avatar';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';

import { ItemCard } from '../item-card/item-card';
import { ParticipantPicker } from '../participant-picker/participant-picker';
import { ResourceCard } from '../resource-card/resource-card';
import { describeReservationError } from '../reservation-error';
import {
  BookedSlot,
  CreatedReservation,
  InventoryOption,
  Participant,
  RentedItem,
  ReservationApi,
  ResourceOption,
  VenueOption,
  overlaps,
} from '../reservation-models';

const BOOKING_DURATION_HOURS = 1;

const MS_PER_HOUR = 3_600_000;

const STEPS = [
  { value: 1, heading: 'Select booking period', progress: 5 },
  { value: 2, heading: 'Select a resource', progress: 25 },
  { value: 3, heading: 'Book items', progress: 50 },
  { value: 4, heading: 'Add participants', progress: 75 },
  { value: 5, heading: 'Review booking', progress: 99 },
] as const;

@Component({
  selector: 'app-reservation-create',
  imports: [
    DatePipe,
    DecimalPipe,
    FormsModule,
    RouterLink,
    AvatarModule,
    ButtonModule,
    CardModule,
    DatePickerModule,
    MessageModule,
    ProgressBarModule,
    ProgressSpinnerModule,
    SelectModule,
    TagModule,
    ToastModule,
    ItemCard,
    ParticipantPicker,
    ResourceCard,
  ],
  templateUrl: './reservation-create.html',
  styleUrl: './reservation-create.css',
})
export class ReservationCreate {
  private readonly api = inject(ReservationApi);
  private readonly messages = inject(MessageService);

  protected readonly steps = STEPS;
  protected readonly step = signal<number>(1);

  protected readonly startAt = signal<Date | null>(startOfNextHour());

  protected readonly endAt = computed(() => {
    const start = this.startAt();

    return start ? addHours(start, BOOKING_DURATION_HOURS) : null;
  });

  protected readonly venues = signal<VenueOption[]>([]);
  protected readonly venueId = signal<number | null>(null);
  protected readonly resources = signal<ResourceOption[]>([]);
  protected readonly resourceId = signal<number | null>(null);
  protected readonly bookedSlots = signal<BookedSlot[]>([]);
  protected readonly loadingVenues = signal(false);
  protected readonly loadingResources = signal(false);

  protected readonly inventory = signal<InventoryOption[]>([]);
  protected readonly loadingInventory = signal(false);

  protected readonly quantities = signal<Record<number, number>>({});

  protected readonly participants = signal<Participant[]>([]);

  protected readonly submitting = signal(false);
  protected readonly submitError = signal<string | null>(null);
  protected readonly created = signal<CreatedReservation | null>(null);

  protected readonly currentStep = computed(
    () => STEPS.find((step) => step.value === this.step()) ?? STEPS[0],
  );

  protected readonly progress = computed(() => (this.created() ? 100 : this.currentStep().progress));

  protected readonly periodValid = computed(() => this.startAt() !== null);

  protected readonly durationHours = computed(() => {
    const start = this.startAt();
    const end = this.endAt();

    if (!start || !end) {
      return 0;
    }

    return Math.max(0, (end.getTime() - start.getTime()) / MS_PER_HOUR);
  });

  protected readonly selectedVenue = computed(
    () => this.venues().find((venue) => venue.id === this.venueId()) ?? null,
  );

  protected readonly selectedResource = computed(
    () => this.resources().find((resource) => resource.id === this.resourceId()) ?? null,
  );

  protected labelOf(participant: Participant): string {
    return participant.kind === 'user' ? participant.name : participant.email;
  }

  protected readonly organiserName = computed(() => {
    const organiser = this.participants().find(
      (participant) => participant.kind === 'user' && participant.organiser,
    );

    return organiser?.kind === 'user' ? organiser.name : '';
  });

  protected readonly rentedItems = computed<RentedItem[]>(() =>
    Object.entries(this.quantities())
      .filter(([, quantity]) => quantity > 0)
      .map(([inventoryId, quantity]) => ({ inventoryId: Number(inventoryId), quantity })),
  );

  protected readonly rentedTotal = computed(() =>
    this.rentedItems().reduce((total, item) => total + item.quantity, 0),
  );

  protected readonly estimatedRate = computed(() => {
    const resource = this.selectedResource();

    return resource ? resource.hourlyRate * this.durationHours() : 0;
  });

  protected readonly clashingSlots = computed(() => {
    const start = this.startAt();
    const end = this.endAt();

    if (!start || !end) {
      return [];
    }

    return this.bookedSlots().filter((slot) => overlaps(start, end, slot.start, slot.end));
  });

  protected readonly slotAvailable = computed(() => this.clashingSlots().length === 0);

  protected readonly canAdvance = computed(() => this.stepComplete(this.step()));

  constructor() {
    this.loadVenues();
    this.loadOrganiser();
  }

  protected stepComplete(step: number): boolean {
    switch (step) {
      case 1:
        return this.periodValid();
      case 2:
        return this.resourceId() !== null && this.slotAvailable();
      case 5:
        return this.slotAvailable();
      default:
        return true;
    }
  }

  protected next(): void {
    if (!this.canAdvance() || this.step() >= STEPS.length) {
      return;
    }

    this.step.update((step) => step + 1);
  }

  protected back(): void {
    this.step.update((step) => Math.max(1, step - 1));
  }

  protected onVenueChange(venueId: number | null): void {
    this.venueId.set(venueId);
    this.resourceId.set(null);
    this.resources.set([]);
    this.bookedSlots.set([]);
    this.inventory.set([]);
    this.quantities.set({});

    if (venueId === null) {
      return;
    }

    this.loadingResources.set(true);
    this.loadingInventory.set(true);

    this.api.listResources(venueId).subscribe({
      next: (resources) => {
        this.resources.set(resources);
        this.loadingResources.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loadingResources.set(false);
        this.report('Could not load resources', error);
      },
    });

    this.api.listInventory(venueId).subscribe({
      next: (items) => {
        this.inventory.set(items);
        this.loadingInventory.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loadingInventory.set(false);
        this.report('Could not load items', error);
      },
    });
  }

  protected selectResource(resource: ResourceOption): void {
    this.resourceId.set(resource.id);
    this.bookedSlots.set([]);

    this.api.listBookedSlots(resource.id).subscribe({
      next: (slots) => this.bookedSlots.set(slots),
      error: (error: HttpErrorResponse) => this.report('Could not load existing bookings', error),
    });
  }

  protected quantityOf(item: InventoryOption): number {
    return this.quantities()[item.id] ?? 0;
  }

  protected setQuantity(item: InventoryOption, quantity: number): void {
    this.quantities.update((quantities) => ({ ...quantities, [item.id]: quantity }));
  }

  protected nameOfItem(inventoryId: number): string {
    return this.inventory().find((item) => item.id === inventoryId)?.name ?? '';
  }

  protected removeItem(inventoryId: number): void {
    this.quantities.update((quantities) => ({ ...quantities, [inventoryId]: 0 }));
  }

  protected addParticipant(participant: Participant): void {
    const email = participant.email.toLowerCase();

    if (this.participants().some((existing) => existing.email.toLowerCase() === email)) {
      return;
    }

    this.participants.update((participants) => [...participants, participant]);
  }

  protected removeParticipant(participant: Participant): void {
    if (participant.kind === 'user' && participant.organiser) {
      return;
    }

    this.participants.update((participants) =>
      participants.filter((existing) => existing !== participant),
    );
  }

  protected submit(): void {
    const start = this.startAt();
    const end = this.endAt();
    const resourceId = this.resourceId();

    if (this.submitting() || !start || !end || resourceId === null || !this.slotAvailable()) {
      return;
    }

    if (!this.periodValid()) {
      return;
    }

    this.submitting.set(true);
    this.submitError.set(null);

    this.api
      .createReservation({
        resourceId,
        start: start.toISOString(),
        end: end.toISOString(),
        participants: this.participants(),
        items: this.rentedItems(),
      })
      .subscribe({
        next: (reservation) => {
          this.submitting.set(false);
          this.created.set(reservation);
          this.messages.add({
            severity: 'success',
            summary: 'Booking confirmed',
            detail: `${this.selectedResource()?.name} is booked.`,
          });
        },
        error: (error: HttpErrorResponse) => {
          this.submitting.set(false);
          this.submitError.set(describeReservationError(error));
          this.messages.add({
            severity: 'error',
            summary: 'Could not create the reservation',
            detail: describeReservationError(error),
          });
        },
      });
  }

  protected createAnother(): void {
    this.created.set(null);
    this.submitError.set(null);
    this.resourceId.set(null);
    this.bookedSlots.set([]);
    this.quantities.set({});
    this.participants.update((participants) =>
      participants.filter((participant) => participant.kind === 'user' && participant.organiser),
    );
    this.step.set(1);
  }

  private loadVenues(): void {
    this.loadingVenues.set(true);

    this.api.listVenues().subscribe({
      next: (venues) => {
        this.venues.set(venues);
        this.loadingVenues.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.loadingVenues.set(false);
        this.report('Could not load venues', error);
      },
    });
  }

  private loadOrganiser(): void {
    this.api.organiser().subscribe({
      next: (organiser) =>
        this.addParticipant({
          kind: 'user',
          userId: organiser.id,
          name: organiser.name,
          email: organiser.email,
          organiser: true,
        }),
      error: (error: HttpErrorResponse) => this.report('Could not load your account', error),
    });
  }

  private report(summary: string, error: HttpErrorResponse): void {
    this.messages.add({
      severity: 'error',
      summary,
      detail: describeReservationError(error),
    });
  }
}

function startOfNextHour(): Date {
  const date = new Date();
  date.setHours(date.getHours() + 1, 0, 0, 0);

  return date;
}

function addHours(date: Date, hours: number): Date {
  const result = new Date(date);
  result.setHours(result.getHours() + hours);

  return result;
}
