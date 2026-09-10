import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import {TitleCasePipe} from '@angular/common';
import {ResourceService} from './resource.service';
import {DialogModule} from 'primeng/dialog';
import {ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormControl} from '@angular/forms';
import {Resource} from './resources.model';
import {ActivatedRoute} from '@angular/router';

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
  resourceService = inject(ResourceService);
  formBuilder = inject(FormBuilder);
  route = inject(ActivatedRoute);

  resourceList: Resource[] = [];
  venueName = '...';
  venueId!: number;

  currentPage = 0;
  totalPages = 0;
  visiblePages: (number | string)[] = [];

  isDeleteModalVisible = false;
  isFormModalVisible = false;
  isInfoModalVisible = false;
  selectedResource: Resource | null = null;

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

  /**
   * Loads the details of the current venue from the backend.
   * Automatically updates the venue name in the UI.
   */
  loadVenueDetails() {
    this.resourceService.getVenueById(this.venueId).subscribe({
      next: (venue) => {
        this.venueName = venue.name || `Venue #${this.venueId}`;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading venue details:', err);
        // Fallback
        this.venueName = `Venue #${this.venueId}`;
        this.cdr.detectChanges();
      }
    });
  }

  /**
   * Loads resources for the current venue and handles pagination metadata.
   * @param pageIndex The 0-based page number to fetch.
   */
  loadResources(pageIndex: number = 0) {
    this.resourceService.getResources(this.venueId, pageIndex).subscribe({
      next: (data) => {
        this.resourceList = data.content;
        this.currentPage = data.number;
        this.totalPages = data.totalPages;

        this.generateVisiblePages();
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error loading resources', err)
    });
  }

  /**
   * Navigates to a specific page of resources.
   * @param pageIndex The target page index.
   */
  goToPage(pageIndex: number) {
    if (pageIndex >= 0 && pageIndex < this.totalPages && pageIndex !== this.currentPage) {
      this.loadResources(pageIndex);
    }
  }

  /**
   * Calculates which pagination buttons to display based on the current page and total pages.
   */
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

  /**
   * Opens the form modal for adding a new resource.
   */
  openAddModal() {
    this.resourceForm.reset();
    this.selectedResource = null;
    this.isFormModalVisible = true;
  }

  /**
   * Opens the form modal and populates it with existing resource data for editing.
   * @param resource The resource to edit.
   */
  openUpdateModal(resource: Resource) {
    this.selectedResource = resource;
    this.resourceForm.patchValue(resource);
    this.isFormModalVisible = true;
  }

  /**
   * Opens the info modal to display scheduled slots for a resource.
   * @param resource The resource to view.
   */
  openInfoModal(resource: Resource) {
    this.selectedResource = resource;
    this.isInfoModalVisible = true;
  }

  /**
   * Opens the confirmation modal before deleting a resource.
   * @param resource The resource to delete.
   */
  openDeleteModal(resource: Resource) {
    this.selectedResource = resource;
    this.isDeleteModalVisible = true;
  }

  /**
   * Submits the form data to either create a new resource or update an existing one.
   */
  saveResource() {
    if (this.resourceForm.valid) {
      const formData = this.resourceForm.value;
      if (this.selectedResource === null) {
        // add
        this.resourceService.addResource(formData, this.venueId).subscribe(() => {
          this.loadResources();
          this.isFormModalVisible = false;
        });
      } else {
        // update
        this.resourceService.updateResource({...formData, id: this.selectedResource.id}, this.venueId).subscribe(() => {
          this.loadResources();
          this.isFormModalVisible = false;
        });
      }
    }
  }

  /**
   * Confirms the deletion of the selected resource and refreshes the list.
   */
  confirmDelete() {
    if (this.selectedResource) {
      this.resourceService.deleteResource(this.selectedResource.id).subscribe(() => {
        this.loadResources();
        this.isDeleteModalVisible = false;
      });
    }
  }
}
