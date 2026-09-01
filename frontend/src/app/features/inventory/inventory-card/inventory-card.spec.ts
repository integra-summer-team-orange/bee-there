import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InventoryCard, InventoryItem } from './inventory-card';
import { By } from '@angular/platform-browser';

describe('InventoryCard', () => {
  let component: InventoryCard;
  let fixture: ComponentFixture<InventoryCard>;

  const mockItem: InventoryItem = {
    id: '1',
    venue_id: 'v1',
    name: 'Test Item',
    total_quantity: 100,
    available_quantity: 50
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InventoryCard]
    }).compileComponents();

    fixture = TestBed.createComponent(InventoryCard);
    component = fixture.componentInstance;
    component.item = mockItem;
    fixture.detectChanges();
  });

  it('should instantiate correctly', () => {
    expect(component).toBeTruthy();
  });

  it('should render the item name in template', () => {
    const title = fixture.debugElement.query(By.css('.p-card-title')).nativeElement;
    expect(title.textContent).toContain('Test Item');
  });

  it('should calculate percentage correctly', () => {
    expect(component.percentage).toBe(50);
  });

  it('should return correct status color class based on stock level', () => {
    expect(component.statusColorClass).toBe('bg-warning');

    component.item = { ...mockItem, available_quantity: 80 };
    expect(component.statusColorClass).toBe('bg-success');

    component.item = { ...mockItem, available_quantity: 10 };
    expect(component.statusColorClass).toBe('bg-danger');
  });

  it('should emit item on edit action', () => {
    let emittedItem: InventoryItem | null = null;
    component.edit.subscribe((val) => (emittedItem = val));

    const editBtn = fixture.debugElement.queryAll(By.css('p-button'))[0];
    editBtn.triggerEventHandler('onClick', null);

    expect(emittedItem).toEqual(mockItem);
  });

  it('should emit item id on delete action', () => {
    let deletedId: string | null = null;
    component.delete.subscribe((val) => (deletedId = val));

    const deleteBtn = fixture.debugElement.queryAll(By.css('p-button'))[1];
    deleteBtn.triggerEventHandler('onClick', null);

    expect(deletedId).toBe('1');
  });
});
