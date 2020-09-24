import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {SensitivityComponent} from './sensitivity.component';

describe('SensitivityComponent', () => {
  let component: SensitivityComponent;
  let fixture: ComponentFixture<SensitivityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SensitivityComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SensitivityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
