import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerListaClientesComponent } from './manager-client-list.component';

describe('ClientListComponent', () => {
  let component: ManagerListaClientesComponent;
  let fixture: ComponentFixture<ManagerListaClientesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManagerListaClientesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagerListaClientesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
