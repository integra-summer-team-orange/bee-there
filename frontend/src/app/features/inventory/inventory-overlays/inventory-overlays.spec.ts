import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InventoryOverlays } from './inventory-overlays';
import { InventoryDto } from '../../../../api/generated';

describe('InventoryOverlays', () => {
  let component: InventoryOverlays;
  let fixture: ComponentFixture<InventoryOverlays>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryOverlays],
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryOverlays);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('headerText', () => {
    it('should return correct title for create, edit, and delete modes', () => {
      expect(component.headerText).toBe('Add New Item');

      fixture.componentRef.setInput('mode', 'edit');
      expect(component.headerText).toBe('Edit Item');

      fixture.componentRef.setInput('mode', 'delete');
      expect(component.headerText).toBe('Delete Item');
    });
  });

  describe('isQuantityInvalid', () => {
    it('should validate boundary quantities and relations', () => {
      // Valid
      component.item.set({ totalQuantity: 20, availableQuantity: 10 });
      expect(component.isQuantityInvalid).toBe(false);

      // Invalid: available > total
      component.item.set({ totalQuantity: 10, availableQuantity: 20 });
      expect(component.isQuantityInvalid).toBe(true);

      // Invalid: negative values
      component.item.set({ totalQuantity: -1, availableQuantity: 0 });
      expect(component.isQuantityInvalid).toBe(true);

      component.item.set({ totalQuantity: 10, availableQuantity: -5 });
      expect(component.isQuantityInvalid).toBe(true);

      // Always valid in delete mode
      fixture.componentRef.setInput('mode', 'delete');
      expect(component.isQuantityInvalid).toBe(false);
    });
  });

  describe('model updates', () => {
    it('should update name, totalQuantity, and availableQuantity on item model', () => {
      component.updateName('Tennis Balls');
      expect(component.item().name).toBe('Tennis Balls');

      component.updateTotal(100);
      expect(component.item().totalQuantity).toBe(100);

      component.updateAvailable(45);
      expect(component.item().availableQuantity).toBe(45);
    });
  });

  describe('onHide', () => {
    it('should set visible model to false', () => {
      component.visible.set(true);
      component.onHide();
      expect(component.visible()).toBe(false);
    });
  });

  describe('onConfirm', () => {
    it('should emit confirm with item data and hide overlay when valid', () => {
      let emittedItem: Partial<InventoryDto> | null = null;
      component.confirm.subscribe((val) => (emittedItem = val));

      component.visible.set(true);
      component.item.set({ name: 'Shuttlecocks', totalQuantity: 50, availableQuantity: 20 });

      component.onConfirm();

      expect(emittedItem).toEqual({ name: 'Shuttlecocks', totalQuantity: 50, availableQuantity: 20 });
      expect(component.visible()).toBe(false);
    });

    it('should not emit confirm if name is missing or whitespace', () => {
      let emitted = false;
      component.confirm.subscribe(() => (emitted = true));

      component.item.set({ name: '   ', totalQuantity: 10, availableQuantity: 5 });
      component.onConfirm();

      expect(emitted).toBe(false);
    });

    it('should not emit confirm if quantity is invalid', () => {
      let emitted = false;
      component.confirm.subscribe(() => (emitted = true));

      component.item.set({ name: 'Footballs', totalQuantity: 5, availableQuantity: 15 });
      component.onConfirm();

      expect(emitted).toBe(false);
    });

    it('should emit numeric id on delete mode and hide overlay', () => {
      let deletedId: number | null = null;
      component.delete.subscribe((id) => (deletedId = id));

      fixture.componentRef.setInput('mode', 'delete');
      component.visible.set(true);
      component.item.set({ id: 42, name: 'To Delete' });

      component.onConfirm();

      expect(deletedId).toBe(42);
      expect(component.visible()).toBe(false);
    });

    it('should handle deletion when item id is 0 without falsy failure', () => {
      let deletedId: number | null = null;
      component.delete.subscribe((id) => (deletedId = id));

      fixture.componentRef.setInput('mode', 'delete');
      component.item.set({ id: 0, name: 'Zero ID Item' });

      component.onConfirm();

      expect(deletedId).toBe(0);
    });
  });
});
