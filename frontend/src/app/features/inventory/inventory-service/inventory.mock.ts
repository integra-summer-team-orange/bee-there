import { InventoryDto } from '../../../../api/generated';

export const MOCK_INVENTORY_ITEMS: InventoryDto[] = [
  { id: 1, venueId: 1, name: 'Basketballs', totalQuantity: 60, availableQuantity: 20 },
  { id: 2, venueId: 1, name: 'Tennis Rackets', totalQuantity: 12, availableQuantity: 10 },
  { id: 3, venueId: 1, name: 'Footballs', totalQuantity: 120, availableQuantity: 0 },
  { id: 4, venueId: 1, name: 'Badminton Rackets', totalQuantity: 8, availableQuantity: 1 },
  { id: 5, venueId: 1, name: 'Volleyballs', totalQuantity: 30, availableQuantity: 15 },
  { id: 6, venueId: 1, name: 'Table Tennis Paddles', totalQuantity: 16, availableQuantity: 14 },
  { id: 6, venueId: 2, name: '[Should not appear]', totalQuantity: 0, availableQuantity: 0 },
];
