import { TestBed } from '@angular/core/testing';

import { BackendClientService } from './backend-client.service';

describe('BackendClientService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: BackendClientService = TestBed.get(BackendClientService);
    expect(service).toBeTruthy();
  });
});
