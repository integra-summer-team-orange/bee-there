import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { TitleCasePipe } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { VenuesService, ResourcesService } from '../../../api/generated';
import { ResourceDto } from '../../../api/generated';

/**
 * Component for displaying and managing resources of a specific venue.
 */
@Component({
  selector: 'app-resources',
  imports: [DialogModule, ReactiveFormsModule, TitleCasePipe],
  templateUrl: './resources.html',
  styleUrl: './resources.css',
})
export class Resources implements OnInit {
  cdr = inject(ChangeDetectorRef);
  venuesService = inject(VenuesService);
  resourcesService = inject(ResourcesService);

  formBuilder = inject(FormBuilder);
  route = inject(ActivatedRoute);

  resourceList: ResourceDto[] = [];
  venueName = '...';
  venueId!: number;

  currentPage = 0;
  totalPages = 0;
  visiblePages: (number | string)[] = [];

  isDeleteModalVisible = false;
  isFormModalVisible = false;
  isInfoModalVisible = false;
  selectedResource: ResourceDto | null = null;

  resourceForm: FormGroup = this.formBuilder.group({
    name: new FormControl('', Validators.required),
    activityType: new FormControl('', Validators.required),
    activityDescription: new FormControl(''),
    type: new FormControl('', Validators.required),
    capacity: new FormControl('', Validators.required),
    hourlyRate: new FormControl('', Validators.required)
  });

  ngOnInit() {
    this.venueId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadVenueDetails();
    this.loadResources();
  }

  loadVenueDetails() {
    this.venuesService.getVenueById(this.venueId).subscribe({
      next: (venue) => {
        this.venueName = venue.name || `Venue #${this.venueId}`;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading venue details:', err);
        this.venueName = `Venue #${this.venueId}`;
        this.cdr.detectChanges();
      }
    });
  }

  loadResources(pageIndex: number = 0) {
    this.venuesService.getResourcesByVenue(this.venueId, pageIndex, 10).subscribe({
      next: (data: any) => {
        this.resourceList = data.content || [];
        this.currentPage = data.number || 0;
        this.totalPages = data.totalPages || 1;

        this.generateVisiblePages();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error loading resources', err)
    });
  }

  goToPage(pageIndex: number) {
    if (pageIndex >= 0 && pageIndex < this.totalPages && pageIndex !== this.currentPage) {
      this.loadResources(pageIndex);
    }
  }

  generateVisiblePages() {
    const pages: (number | string)[] = [];
    const total = this.totalPages;
    const current = this.currentPage + 1;

    if (total <= 5) {
      for (let i = 1; i <= total; i++) pages.push(i);
    } else {
      if (current <= 3) {
        pages.push(1, 2, 3, '...', total - 1, total);
      } else if (current >= total - 2) {
        pages.push(1, 2, '...', total - 2, total - 1, total);
      } else {
        pages.push(1, '...', current, '...', total);
      }
    }
    this.visiblePages = pages;
  }

  openAddModal() {
    this.resourceForm.reset();
    this.selectedResource = null;
    this.isFormModalVisible = true;
  }

  openUpdateModal(resource: ResourceDto) {
    this.selectedResource = resource;
    this.resourceForm.patchValue(resource);
    this.isFormModalVisible = true;
  }

  openInfoModal(resource: ResourceDto) {
    this.selectedResource = resource;
    this.isInfoModalVisible = true;
  }

  openDeleteModal(resource: ResourceDto) {
    this.selectedResource = resource;
    this.isDeleteModalVisible = true;
  }

  saveResource() {
    if (this.resourceForm.valid) {
      const formData = this.resourceForm.value;

      const basePayload = {
        ...formData,
        venueId: this.venueId,
        capacity: Number(formData.capacity),
        hourlyRate: Number(formData.hourlyRate)
      };

      if (this.selectedResource === null) {
        // ADD
        this.resourcesService.createResource(basePayload as ResourceDto).subscribe({
          next: () => {
            this.isFormModalVisible = false;
            this.loadResources(this.currentPage);
          },
          error: (err) => console.error('Eroare la adaugare:', err)
        });
      } else {
        // UPDATE
        const updatePayload = {
          ...basePayload,
          id: this.selectedResource.id
        };

        this.resourcesService.updateResource(this.selectedResource.id!, updatePayload as ResourceDto).subscribe({
          next: () => {
            this.isFormModalVisible = false;
            this.loadResources(this.currentPage);
          },
          error: (err) => console.error('Eroare la update:', err)
        });
      }
    }
  }

  confirmDelete() {
    if (this.selectedResource && this.selectedResource.id) {
      this.resourcesService.deleteResource(this.selectedResource.id).subscribe({
        next: () => {
          this.isDeleteModalVisible = false;
          this.loadResources(this.currentPage);
        },
        error: (err) => console.error('Eroare la stergere:', err)
      });
    }
  }
}
