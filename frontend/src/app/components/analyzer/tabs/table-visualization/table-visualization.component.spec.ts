import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TableVisualizationComponent} from './table-visualization.component';

describe('TableVisualizationComponent', () => {
  let component: TableVisualizationComponent;
  let fixture: ComponentFixture<TableVisualizationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TableVisualizationComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TableVisualizationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
