import { Injectable } from '@angular/core';
import {Resource} from './resources.model';

@Injectable({
  providedIn: 'root',
})
export class ResourceService {
  private resources: Resource[] = [
    {
      id: 1,
      name: 'Basketball Court A',
      activityType: 'PLAYING',
      activityDescription: '5v5 court with benches for other people to watch',
      type: 'INDOOR_SPORT',
      capacity: 20,
      hourlyRate: 75
    },
    {
      id: 2,
      name: 'Tennis Court Pro',
      activityType: 'TRAINING',
      activityDescription: 'Professional clay court with night lighting',
      type: 'OUTDOOR_SPORT',
      capacity: 4,
      hourlyRate: 50
    },
    {
      id: 3,
      name: 'Football Field Main',
      activityType: 'MATCH',
      activityDescription: 'Full-size natural grass football field with grandstands',
      type: 'OUTDOOR_SPORT',
      capacity: 30,
      hourlyRate: 150
    },
    {
      id: 4,
      name: 'Badminton Hall B',
      activityType: 'PLAYING',
      activityDescription: 'Indoor synthetic court with professional netting',
      type: 'INDOOR_SPORT',
      capacity: 6,
      hourlyRate: 40
    }
  ];

  getResources(): Resource[] {
    return this.resources;
  };

  addResource(newResource: Resource) {
    // fake unique id just for frontend testing
    newResource.id = new Date().getTime();
    this.resources.push(newResource);
  };

  updateResource(updatedResource: Resource) {
    const index = this.resources.findIndex(r => r.id === updatedResource.id);
    if (index !== -1) {
      this.resources[index] = updatedResource;
    }
  };

  deleteResource(id: number) {
    this.resources = this.resources.filter(r => r.id !== id);
  };
}
