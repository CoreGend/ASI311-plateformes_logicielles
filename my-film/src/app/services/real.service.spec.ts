import { TestBed } from '@angular/core/testing';

import { RealService } from './real.service';

describe('RealService', () => {
  let service: RealService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RealService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
