import {TestBed} from '@angular/core/testing';

import {SensitivityService} from './sensitivity.service';

describe('SensitivityService', () => {
  let service: SensitivityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SensitivityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
