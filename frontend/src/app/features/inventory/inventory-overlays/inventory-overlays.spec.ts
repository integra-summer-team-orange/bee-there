import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InventoryOverlays } from './inventory-overlays';

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

  it('should instantiate correctly', () => {
    expect(component).toBeTruthy();
  });

  it('should compute header text based on mode', () => {
    component.mode = 'create';
    expect(component.headerText).toBe('Add New Item');

    component.mode = 'edit';
    expect(component.headerText).toBe('Edit Item');

    component.mode = 'delete';
    expect(component.headerText).toBe('Delete Item');
  });

  it('should emit item on confirmation during create/edit', () => {
    let emittedItem: any = null;
    let closedVisibility: boolean | null = null;

    // Direct event subscriptions
    component.confirm.subscribe((val) => (emittedItem = val));
    component.visibleChange.subscribe((val) => (closedVisibility = val));

    component.mode = 'create';
    component.item = { name: 'New Item' };
    component.onConfirm();

    expect(emittedItem).toEqual({ name: 'New Item' });
    expect(closedVisibility).toBe(false);
  });

  it('should emit id on confirmation during delete', () => {
    let deletedId: string | null = null;
    let closedVisibility: boolean | null = null;

    // Direct event subscriptions
    component.delete.subscribe((val) => (deletedId = val));
    component.visibleChange.subscribe((val) => (closedVisibility = val));

    component.mode = 'delete';
    component.item = { id: '123', name: 'Delete Me' };
    component.onConfirm();

    expect(deletedId).toBe('123');
    expect(closedVisibility).toBe(false);
  });
});
