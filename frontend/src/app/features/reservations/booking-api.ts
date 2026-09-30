import { Observable } from 'rxjs';

export type BookingStatus = 'ACTIVE' | 'CANCELLED';

export interface BookingPerson {
  userId: number;
  name: string;
  email: string;
}

export interface BookedItem {
  inventoryId: number;
  name: string;
  quantity: number;
}

export interface Booking {
  id: number;
  venueId: number;
  venueName: string;
  venueAddress: string;
  venueDescription: string;
  resourceId: number;
  resourceName: string;
  start: string;
  end: string;
  status: BookingStatus;
  organizer: BookingPerson;
  item: BookedItem | null;
  participants: readonly BookingPerson[];
  requests: readonly BookingPerson[];
}

export interface StockItem {
  id: number;
  name: string;
  totalQuantity: number;
  availableQuantity: number;
}

export interface ItemChoice {
  inventoryId: number;
  quantity: number;
}

/** What the reservation overview, detail and item screens need from the backend. */
export abstract class BookingApi {
  abstract listMine(term?: string): Observable<Booking[]>;
  abstract get(id: number): Observable<Booking>;
  abstract cancel(id: number): Observable<Booking>;
  abstract listStock(venueId: number): Observable<StockItem[]>;
  abstract setItem(id: number, choice: ItemChoice | null): Observable<Booking>;
  abstract addParticipant(id: number, email: string): Observable<Booking>;
  abstract removeParticipant(id: number, userId: number): Observable<Booking>;
  abstract approveRequests(id: number, userIds: readonly number[]): Observable<Booking>;
  abstract declineRequest(id: number, userId: number): Observable<Booking>;
}
