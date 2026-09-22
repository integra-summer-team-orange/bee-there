import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { InventoryWrapperService } from './inventory-wrapper-service';
import {
  InventoryService as InventoryApiService,
  InventoryDto,
  VenuesService
} from '../../../../api/generated';

describe('InventoryWrapperService', () => {
  let service: InventoryWrapperService;
  let apiMock: {
    getAllInventoryItems: ReturnType<typeof vi.fn>;
    createInventoryItem: ReturnType<typeof vi.fn>;
    updateInventoryItem: ReturnType<typeof vi.fn>;
    deleteInventoryItem: ReturnType<typeof vi.fn>;
  };
  let venuesApiMock: {
    getInventoryByVenue: ReturnType<typeof vi.fn>;
    getVenueById: ReturnType<typeof vi.fn>;
  };

  const mockItem: InventoryDto = {
    id: 1,
    venueId: 1,
    name: 'New Test Item',
    totalQuantity: 10,
    availableQuantity: 5,
  };

  beforeEach(() => {
    apiMock = {
      getAllInventoryItems: vi.fn(),
      createInventoryItem: vi.fn(),
      updateInventoryItem: vi.fn(),
      deleteInventoryItem: vi.fn(),
    };

    venuesApiMock = {
      getInventoryByVenue: vi.fn(),
      getVenueById: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        InventoryWrapperService,
        { provide: InventoryApiService, useValue: apiMock },
        { provide: VenuesService, useValue: venuesApiMock },
      ],
    });

    service = TestBed.inject(InventoryWrapperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
    expect(service.items()).toEqual([]);
  });

  it('should add a new item', async () => {
    service.setVenueId(1);
    apiMock.createInventoryItem.mockReturnValue(of(mockItem));

    await service.addItem('New Test Item', 10, 5);

    expect(service.items().length).toBe(1);
    expect(service.items()[0].name).toBe('New Test Item');
  });

  it('should update an existing item', async () => {
    service.setVenueId(1);
    apiMock.createInventoryItem.mockReturnValue(of(mockItem));
    await service.addItem('New Test Item', 10, 5);

    const item = service.items()[0];
    const updated: InventoryDto = { ...item, name: 'Updated Name' };
    apiMock.updateInventoryItem.mockReturnValue(of(updated));

    await service.updateItem(updated);

    expect(service.items().find((i) => i.id === item.id)?.name).toBe('Updated Name');
  });

  it('should delete an item', async () => {
    service.setVenueId(1);
    apiMock.createInventoryItem.mockReturnValue(of(mockItem));
    await service.addItem('New Test Item', 10, 5);

    const initialCount = service.items().length;
    const idToDelete = service.items()[0].id!;
    apiMock.deleteInventoryItem.mockReturnValue(of(undefined));

    await service.deleteItem(idToDelete);

    expect(service.items().length).toBe(initialCount - 1);
    expect(service.items().find((i) => i.id === idToDelete)).toBeUndefined();
  });
});
