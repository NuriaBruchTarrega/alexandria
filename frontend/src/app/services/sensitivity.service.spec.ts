import {TestBed} from '@angular/core/testing';

import {ExperimentsService} from './experiments.service';

describe('SensitivityService', () => {
  let service: ExperimentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExperimentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
