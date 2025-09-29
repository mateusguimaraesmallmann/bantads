import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerClientDetailsComponent } from './manager-client-details.component';

describe('ManagerClientDetailsComponent', () => {
  let component: ManagerClientDetailsComponent;
  let fixture: ComponentFixture<ManagerClientDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerClientDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerClientDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
