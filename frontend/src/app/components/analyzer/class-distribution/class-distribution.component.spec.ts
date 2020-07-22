import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {ClassDistributionComponent} from './class-distribution.component';

describe('ClassDistributionComponent', () => {
  let component: ClassDistributionComponent;
  let fixture: ComponentFixture<ClassDistributionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ClassDistributionComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClassDistributionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
