import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InventoryOverview } from './inventory-overview';
import { provideRouter } from '@angular/router';

describe('InventoryOverview', () => {
  let component: InventoryOverview;
  let fixture: ComponentFixture<InventoryOverview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryOverview],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryOverview);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should filter items based on search query', () => {
    component.searchQuery.set('basket');
    expect(component.filteredItems().length).toBe(1);
    expect(component.filteredItems()[0].name).toBe('Basketballs');
  });

  it('should paginate items', () => {
    component.first.set(0);
    component.rows.set(2);
    fixture.detectChanges();
    expect(component.paginatedItems().length).toBe(2);

    component.first.set(2);
    fixture.detectChanges();
    expect(component.paginatedItems().length).toBe(2);
    expect(component.paginatedItems()[0].id).toBe('3');
  });

  it('should add a new item', () => {
    const initialCount = component.items().length;
    component.addItem('New Test Item', 10, 5);
    expect(component.items().length).toBe(initialCount + 1);
    expect(component.items()[0].name).toBe('New Test Item');
  });

  it('should update an existing item', () => {
    const item = component.items()[0];
    const updated = { ...item, name: 'Updated Name' };
    component.updateItem(updated);
    expect(component.items()[0].name).toBe('Updated Name');
  });

  it('should delete an item', () => {
    const initialCount = component.items().length;
    const idToDelete = component.items()[0].id;
    component.deleteItem(idToDelete);
    expect(component.items().length).toBe(initialCount - 1);
    expect(component.items().find(i => i.id === idToDelete)).toBeUndefined();
  });

  it('should open create overlay', () => {
    component.openCreate();
    expect(component.overlayVisible).toBeTruthy();
    expect(component.overlayMode).toBe('create');
    expect(component.selectedItem.name).toBe('');
  });

  it('should open edit overlay', () => {
    const item = component.items()[0];
    component.openEdit(item);
    expect(component.overlayVisible).toBeTruthy();
    expect(component.overlayMode).toBe('edit');
    expect(component.selectedItem.id).toBe(item.id);
  });
});
