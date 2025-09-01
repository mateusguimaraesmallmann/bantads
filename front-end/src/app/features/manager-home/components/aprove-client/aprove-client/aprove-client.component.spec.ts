import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AproveClientComponent } from './aprove-client.component';

describe('AproveClientComponent', () => {
  let component: AproveClientComponent;
  let fixture: ComponentFixture<AproveClientComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AproveClientComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AproveClientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
