import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MyMaterialModule } from './material.module';
import { ImagePicker } from './components/image-picker/image-picker.component';
import { VerifyMobileComponent } from './components/verify-mobile/verify-mobile.component';
import { CaptchaComponent } from './components/captcha/captcha.component';
import { CountDownButtonComponent } from './components/count-down-button/count-down-button.component';

const MODULES = [
  CommonModule,
  ReactiveFormsModule,
  FormsModule,
  MyMaterialModule
];

const COMPONENTS = [
  ImagePicker,
  VerifyMobileComponent,
  CaptchaComponent,
  CountDownButtonComponent
];

@NgModule({
  declarations: COMPONENTS,
  imports: [...MODULES],
  exports: [...MODULES, ...COMPONENTS]
})
export class SharedModule {}
