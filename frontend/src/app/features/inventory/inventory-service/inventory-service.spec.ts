import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { InventoryService } from './inventory-service';
import { InventoryService as InventoryApiService, InventoryDto } from '../../../../api/generated';

describe('InventoryService', () => {
  let service: InventoryService;
  let apiMock: {
    getAllInventoryItems: ReturnType<typeof vi.fn>;
    createInventoryItem: ReturnType<typeof vi.fn>;
    updateInventoryItem: ReturnType<typeof vi.fn>;
    deleteInventoryItem: ReturnType<typeof vi.fn>;
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

    TestBed.configureTestingModule({
      providers: [
        InventoryService,
        { provide: InventoryApiService, useValue: apiMock },
      ],
    });

    service = TestBed.inject(InventoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
    expect(service.items()).toEqual([]);
  });

  it('should add a new item', async () => {
    apiMock.createInventoryItem.mockReturnValue(of(mockItem));

    await service.addItem('New Test Item', 10, 5);

    expect(service.items().length).toBe(1);
    expect(service.items()[0].name).toBe('New Test Item');
  });

  it('should update an existing item', async () => {
    apiMock.createInventoryItem.mockReturnValue(of(mockItem));
    await service.addItem('New Test Item', 10, 5);

    const item = service.items()[0];
    const updated: InventoryDto = { ...item, name: 'Updated Name' };
    apiMock.updateInventoryItem.mockReturnValue(of(updated));

    await service.updateItem(updated);

    expect(service.items().find((i) => i.id === item.id)?.name).toBe('Updated Name');
  });

  it('should delete an item', async () => {
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
