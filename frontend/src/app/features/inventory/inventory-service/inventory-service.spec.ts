import { TestBed } from '@angular/core/testing';
import { InventoryService } from './inventory-service';

describe('InventoryService', () => {
  let service: InventoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [InventoryService]
    });
    service = TestBed.inject(InventoryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should add a new item', () => {
    const initialCount = service.items().length;
    service.addItem('New Test Item', 10, 5);
    expect(service.items().length).toBe(initialCount + 1);
    expect(service.items()[0].name).toBe('New Test Item');
  });

  it('should update an existing item', () => {
    const item = service.items()[0];
    const updated = { ...item, name: 'Updated Name' };
    service.updateItem(updated);
    expect(service.items().find(i => i.id === item.id)?.name).toBe('Updated Name');
  });

  it('should delete an item', () => {
    const initialCount = service.items().length;
    const idToDelete = service.items()[0].id;
    service.deleteItem(idToDelete);
    expect(service.items().length).toBe(initialCount - 1);
    expect(service.items().find(i => i.id === idToDelete)).toBeUndefined();
  });
});
