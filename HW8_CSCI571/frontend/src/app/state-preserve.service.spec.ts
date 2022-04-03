import { TestBed } from '@angular/core/testing';

import { StatePreserveService } from './state-preserve.service';

describe('StatePreserveService', () => {
  let service: StatePreserveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatePreserveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
