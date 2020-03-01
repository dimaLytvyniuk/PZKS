import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GraphSystemComponent } from './graph-system.component';

describe('GraphSystemComponent', () => {
  let component: GraphSystemComponent;
  let fixture: ComponentFixture<GraphSystemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GraphSystemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GraphSystemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
