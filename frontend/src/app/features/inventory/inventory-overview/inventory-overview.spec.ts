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
