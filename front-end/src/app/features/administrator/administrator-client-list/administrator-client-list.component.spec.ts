import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdministratorListaClientesComponent } from './administrator-client-list.component';

describe('ClientListComponent', () => {
  let component: AdministratorListaClientesComponent;
  let fixture: ComponentFixture<AdministratorListaClientesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdministratorListaClientesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdministratorListaClientesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
