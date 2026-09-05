import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';
import { InventoryOverview } from './inventory-overview';
import { InventoryService } from '../inventory-service/inventory-service';
import { InventoryDto } from '../../../../api/generated';

describe('InventoryOverview', () => {
  let component: InventoryOverview;
  let fixture: ComponentFixture<InventoryOverview>;

  let mockInventoryService: {
    items: ReturnType<typeof signal<InventoryDto[]>>;
    loading: ReturnType<typeof signal<boolean>>;
    loadItems: () => Promise<void>;
    addItem: (name: string, total: number, available: number) => Promise<void>;
    updateItem: (item: InventoryDto) => Promise<void>;
    deleteItem: (id: number) => Promise<void>;
    getById: (id: number) => InventoryDto | undefined;
  };

  const sampleItems: InventoryDto[] = [
    { id: 1, venueId: 1, name: 'Basketballs', totalQuantity: 60, availableQuantity: 20 },
    { id: 2, venueId: 1, name: 'Tennis Rackets', totalQuantity: 12, availableQuantity: 10 },
    { id: 3, venueId: 1, name: 'Footballs', totalQuantity: 120, availableQuantity: 0 },
    { id: 4, venueId: 1, name: 'Badminton Rackets', totalQuantity: 8, availableQuantity: 1 },
    { id: 5, venueId: 1, name: 'Volleyballs', totalQuantity: 30, availableQuantity: 15 },
    { id: 6, venueId: 1, name: 'Table Tennis Paddles', totalQuantity: 16, availableQuantity: 14 },
    { id: 7, venueId: 1, name: 'Baseballs', totalQuantity: 40, availableQuantity: 40 },
  ];

  beforeEach(async () => {
    const itemsSignal = signal<InventoryDto[]>([...sampleItems]);

    mockInventoryService = {
      items: itemsSignal,
      loading: signal<boolean>(false),
      loadItems: async () => {},
      addItem: async () => {},
      updateItem: async () => {},
      deleteItem: async () => {},
      getById: (id: number) => itemsSignal().find((item) => item.id === id),
    };

    await TestBed.configureTestingModule({
      imports: [InventoryOverview],
      providers: [
        provideRouter([]),
        { provide: InventoryService, useValue: mockInventoryService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryOverview);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and trigger loadItems on init', async () => {
    let loadCalled = false;
    mockInventoryService.loadItems = async () => {
      loadCalled = true;
    };

    component.ngOnInit();
    expect(component).toBeTruthy();
    expect(loadCalled).toBe(true);
  });

  describe('filtering and pagination', () => {
    it('should filter items case-insensitively and trim spaces', () => {
      component.searchQuery.set('  BALLS  ');
      expect(component.filteredItems().length).toBe(4);
      expect(component.filteredItems().map((i) => i.name)).toEqual([
        'Basketballs',
        'Footballs',
        'Volleyballs',
        'Baseballs',
      ]);
    });

    it('should slice paginated items according to first and rows signals', () => {
      component.first.set(0);
      component.rows.set(3);
      expect(component.paginatedItems().length).toBe(3);
      expect(component.paginatedItems()[0].id).toBe(1);

      component.first.set(3);
      expect(component.paginatedItems().length).toBe(3);
      expect(component.paginatedItems()[0].id).toBe(4);
    });

    it('should reset pagination start to 0 when first exceeds filtered item count', () => {
      component.first.set(5);
      component.searchQuery.set('Tennis'); // Matches 2 items: "Tennis Rackets", "Table Tennis Paddles"

      expect(component.filteredItems().length).toBe(2);
      expect(component.paginatedItems().length).toBe(2);
      expect(component.paginatedItems()[0].id).toBe(2);
    });

    it('should update pagination signals on page change event', () => {
      component.onPageChange({ first: 12, rows: 6 });
      expect(component.first()).toBe(12);
      expect(component.rows()).toBe(6);

      // Defaults fallback if properties are undefined
      component.onPageChange({});
      expect(component.first()).toBe(0);
      expect(component.rows()).toBe(6);
    });
  });

  describe('overlay controls', () => {
    it('should configure state for create modal', () => {
      component.openCreate();

      expect(component.overlayVisible()).toBe(true);
      expect(component.overlayMode()).toBe('create');
      expect(component.selectedItem()).toEqual({
        name: '',
        totalQuantity: 0,
        availableQuantity: 0,
      });
    });

    it('should configure state for edit modal with a cloned item', () => {
      const target = sampleItems[0];
      component.openEdit(target);

      expect(component.overlayVisible()).toBe(true);
      expect(component.overlayMode()).toBe('edit');
      expect(component.selectedItem()).toEqual(target);
      expect(component.selectedItem()).not.toBe(target); // Shallow clone check
    });

    it('should configure state for delete modal if item exists', () => {
      component.openDelete(3);

      expect(component.overlayVisible()).toBe(true);
      expect(component.overlayMode()).toBe('delete');
      expect(component.selectedItem().id).toBe(3);
    });

    it('should do nothing on openDelete if item ID is not found', () => {
      component.overlayVisible.set(false);
      component.openDelete(9999);

      expect(component.overlayVisible()).toBe(false);
    });
  });

  describe('CRUD operations', () => {
    it('should delegate create payload to addItem and close overlay', async () => {
      let createdArgs: { name: string; total: number; available: number } | null = null;
      mockInventoryService.addItem = async (name, total, available) => {
        createdArgs = { name, total, available };
      };

      component.openCreate();
      await component.handleConfirm({
        name: 'Pickleballs',
        totalQuantity: 24,
        availableQuantity: 12,
      });

      expect(createdArgs).toEqual({
        name: 'Pickleballs',
        total: 24,
        available: 12,
      });
      expect(component.overlayVisible()).toBe(false);
    });

    it('should delegate edit payload to updateItem and close overlay', async () => {
      let updatedPayload: InventoryDto | null = null;
      mockInventoryService.updateItem = async (item) => {
        updatedPayload = item;
      };

      const editedItem: InventoryDto = {
        id: 2,
        venueId: 1,
        name: 'Pro Tennis Rackets',
        totalQuantity: 15,
        availableQuantity: 12,
      };

      component.openEdit(sampleItems[1]);
      await component.handleConfirm(editedItem);

      expect(updatedPayload).toEqual(editedItem);
      expect(component.overlayVisible()).toBe(false);
    });

    it('should delegate ID to deleteItem and close overlay', async () => {
      let deletedId: number | null = null;
      mockInventoryService.deleteItem = async (id) => {
        deletedId = id;
      };

      component.openDelete(2);
      await component.deleteItem(2);

      expect(deletedId).toBe(2);
      expect(component.overlayVisible()).toBe(false);
    });
  });
});
