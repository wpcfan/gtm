import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MyMaterialModule } from './material.module';
import { ImagePicker } from './components/image-picker/image-picker.component';
import { VerifyMobileComponent } from './components/verify-mobile/verify-mobile.component';

const MODULES = [
  CommonModule,
  ReactiveFormsModule,
  FormsModule,
  MyMaterialModule
];

const COMPONENTS = [ImagePicker, VerifyMobileComponent];

@NgModule({
  declarations: COMPONENTS,
  imports: [...MODULES],
  exports: [...MODULES, ...COMPONENTS]
})
export class SharedModule {}
