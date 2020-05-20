import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GantDiagramComponent } from './gant-diagram.component';

describe('GantDiagramComponent', () => {
  let component: GantDiagramComponent;
  let fixture: ComponentFixture<GantDiagramComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GantDiagramComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GantDiagramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
