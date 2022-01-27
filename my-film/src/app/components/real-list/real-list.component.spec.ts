import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RealListComponent } from './real-list.component';

describe('RealListComponent', () => {
  let component: RealListComponent;
  let fixture: ComponentFixture<RealListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RealListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RealListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
