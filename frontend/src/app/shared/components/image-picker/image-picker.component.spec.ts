import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ImagePicker } from './image-picker.component';

describe('ImagePicker', () => {
  let component: ImagePicker;
  let fixture: ComponentFixture<ImagePicker>;

  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        declarations: [ImagePicker]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ImagePicker);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
