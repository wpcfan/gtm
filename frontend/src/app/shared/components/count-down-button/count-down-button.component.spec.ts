import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CountDownButtonComponent } from './count-down-button.component';

describe('CountDownButtonComponent', () => {
  let component: CountDownButtonComponent;
  let fixture: ComponentFixture<CountDownButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CountDownButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CountDownButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
