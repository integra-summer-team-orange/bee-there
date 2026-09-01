import {Component, inject} from '@angular/core';
import {TitleCasePipe} from '@angular/common';
import {ResourceService} from './resource.service';
import {DialogModule} from 'primeng/dialog';
import {ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormControl} from '@angular/forms';
import {Resource} from './resources.model';

@Component({
  selector: 'app-resources',
  imports: [DialogModule, ReactiveFormsModule, TitleCasePipe],
  templateUrl: './resources.html',
  styleUrl: './resources.css',
})
export class Resources {
  resourceService = inject(ResourceService);
  resourceList = this.resourceService.getResources();
  venueName = 'Cluj Arena';
  isDeleteModalVisible = false;
  isFormModalVisible = false;
  isInfoModalVisible = false;
  selectedResource: Resource | null = null;
  formBuilder = inject(FormBuilder);

  resourceForm: FormGroup = this.formBuilder.group({
    name: new FormControl('', Validators.required),
    activityType: new FormControl('', Validators.required),
    activityDescription: new FormControl(''),
    type: new FormControl('', Validators.required),
    capacity: new FormControl('', Validators.required),
    hourlyRate: new FormControl('', Validators.required)
  });

  openAddModal() {
    this.resourceForm.reset();
    this.selectedResource = null;
    this.isFormModalVisible = true;
  }

  openUpdateModal(resource: Resource) {
    this.selectedResource = resource;
    this.resourceForm.patchValue(resource);
    this.isFormModalVisible = true;
  }

  openInfoModal(resource: Resource) {
    this.selectedResource = resource;
    this.isInfoModalVisible = true;
  }

  openDeleteModal(resource: Resource) {
    this.selectedResource = resource;
    this.isDeleteModalVisible = true;
  }

  saveResource() {
    if (this.resourceForm.valid) {
      const formData = this.resourceForm.value;
      if (this.selectedResource === null) {
        this.resourceService.addResource(formData);
      } else {
        this.resourceService.updateResource({...formData, id: this.selectedResource.id})
      }

      this.resourceList = this.resourceService.getResources();
      this.isFormModalVisible = false;
    }
  }

  confirmDelete() {
    if (this.selectedResource) {
      this.resourceService.deleteResource(this.selectedResource.id);
      this.resourceList = this.resourceService.getResources();
    }
    this.isDeleteModalVisible = false;
  }
}
