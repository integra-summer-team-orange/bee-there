import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfileOverlay } from './user-profile-overlay';

describe('UserProfileOverlay', () => {
  let component: UserProfileOverlay;
  let fixture: ComponentFixture<UserProfileOverlay>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserProfileOverlay]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserProfileOverlay);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
