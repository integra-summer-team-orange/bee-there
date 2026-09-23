import { Observable } from 'rxjs';

export interface VenueOption {
  id: number;
  name: string;
  address: string;
}

export interface ResourceOption {
  id: number;
  venueId: number;
  name: string;
  activityType: string;
  activityDescription?: string;
  type: string;
  capacity: number;
  hourlyRate: number;
}

export interface InventoryOption {
  id: number;
  venueId: number;
  name: string;
  totalQuantity: number;
  availableQuantity: number;
}

export interface UserOption {
  id: number;
  name: string;
  email: string;
}

export interface BookedSlot {
  resourceId: number;
  start: string;
  end: string;
}

export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export type Participant =
  | {
      readonly kind: 'user';
      readonly userId: number;
      readonly name: string;
      readonly email: string;
      readonly organiser: boolean;
    }
  | { readonly kind: 'email'; readonly email: string };

export interface RentedItem {
  inventoryId: number;
  quantity: number;
}

export interface ReservationDraft {
  resourceId: number;
  start: string;
  end: string;
  participants: readonly Participant[];
  items: readonly RentedItem[];
}

export interface CreatedReservation {
  id: number;
  start: string;
  end: string;
  status: ReservationStatus;
}

export abstract class ReservationApi {
  abstract listVenues(): Observable<VenueOption[]>;

  abstract listResources(venueId: number): Observable<ResourceOption[]>;

  abstract listInventory(venueId: number): Observable<InventoryOption[]>;

  abstract listBookedSlots(resourceId: number): Observable<BookedSlot[]>;

  abstract listUsers(term: string): Observable<UserOption[]>;

  abstract organiser(): Observable<UserOption>;

  abstract createReservation(draft: ReservationDraft): Observable<CreatedReservation>;
}

export function overlaps(
  aStart: string | Date,
  aEnd: string | Date,
  bStart: string | Date,
  bEnd: string | Date,
): boolean {
  return (
    new Date(aStart).getTime() < new Date(bEnd).getTime() &&
    new Date(bStart).getTime() < new Date(aEnd).getTime()
  );
}

export function isPlausibleEmail(value: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim());
}

export function readableResourceType(type: string): string {
  const words = type.replace(/_/g, ' ').toLowerCase();

  return words.charAt(0).toUpperCase() + words.slice(1);
}
