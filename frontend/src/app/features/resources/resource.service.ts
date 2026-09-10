import {inject, Injectable} from '@angular/core';
import {Resource} from './resources.model';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';

/**
 * Service for managing resources via backend API.
 */
@Injectable({
  providedIn: 'root',
})
export class ResourceService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/resources'
  //todo: implement token below
  private token = localStorage.getItem('jwt_token') || 'MY_LOCAL_TOKEN';

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.token}`
    });
  }

  /**
   * Fetches a paginated list of resources for a specific venue.
   * @param venueId The ID of the venue.
   * @param pageNumber The page index (0-based).
   * @returns An observable of the paginated response.
   */
  getResources(venueId: number, pageNumber: number = 0): Observable<any> {
    const url = `http://localhost:8080/api/venues/${venueId}/resources?pageNumber=${pageNumber}&pageSize=10`;
    return this.http.get<any>(url, { headers: this.getHeaders() });
  }

  /**
   * Fetches the details of a specific venue (used to display the venue name).
   * Note: Can be moved to a dedicated VenueService later if requested by the team.
   * @param venueId The ID of the venue.
   * @returns An observable of the venue details.
   */
  getVenueById(venueId: number): Observable<any> {
    const url = `http://localhost:8080/api/venues/${venueId}`;
    return this.http.get<any>(url, { headers: this.getHeaders() });
  }

  /**
   * Creates a new resource for a specific venue.
   * @param newResource The resource data to create.
   * @param venueId The ID of the venue.
   * @returns An observable of the created resource.
   */
  addResource(newResource: any, venueId: number): Observable<Resource> {
    const payload = {
      ...newResource,
      venueId,
      capacity: Number(newResource.capacity),
      hourlyRate: Number(newResource.hourlyRate)
    };
    return this.http.post<Resource>(this.apiUrl, payload, { headers: this.getHeaders() });
  }

  /**
   * Updates an existing resource.
   * @param updatedResource The updated resource data.
   * @param venueId The ID of the venue.
   * @returns An observable of the updated resource.
   */
  updateResource(updatedResource: any, venueId: number): Observable<Resource> {
    const payload = {
      ...updatedResource,
      venueId,
      capacity: Number(updatedResource.capacity),
      hourlyRate: Number(updatedResource.hourlyRate)
    };
    return this.http.put<Resource>(`${this.apiUrl}/${updatedResource.id}`, payload, { headers: this.getHeaders() });
  }

  /**
   * Deletes a resource by its ID.
   * @param id The ID of the resource to delete.
   * @returns An observable that completes upon deletion.
   */
  deleteResource(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}
