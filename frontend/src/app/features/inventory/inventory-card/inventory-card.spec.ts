import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InventoryCard } from './inventory-card';
import { By } from '@angular/platform-browser';
import {InventoryDto} from '../../../../api/generated';

describe('InventoryCard', () => {
  let component: InventoryCard;
  let fixture: ComponentFixture<InventoryCard>;

  const mockItem: InventoryDto = {
    id: 1,
    venueId: 1,
    name: 'Test Item',
    totalQuantity: 100,
    availableQuantity: 50
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

    component.item = { ...mockItem, availableQuantity: 80 };
    expect(component.statusColorClass).toBe('bg-success');

    component.item = { ...mockItem, availableQuantity: 10 };
    expect(component.statusColorClass).toBe('bg-danger');
  });

  it('should emit item on edit action', () => {
    let emittedItem: InventoryDto | null = null;
    component.edit.subscribe((val) => (emittedItem = val));

    const editBtn = fixture.debugElement.queryAll(By.css('p-button'))[0];
    editBtn.triggerEventHandler('onClick', null);

    expect(emittedItem).toEqual(mockItem);
  });

  it('should emit item id on delete action', () => {
    let deletedId: number | null = null;
    component.delete.subscribe((val) => (deletedId = val));

    const deleteBtn = fixture.debugElement.queryAll(By.css('p-button'))[1];
    deleteBtn.triggerEventHandler('onClick', null);

    expect(deletedId).toBe(1);
  });
});
