import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';


const MODULES = [
  CommonModule,
  ReactiveFormsModule,
  FormsModule,
];

const COMPONENTS = [

];

@NgModule({
  declarations: COMPONENTS,
  imports: [...MODULES],
  exports: [
    ...MODULES,
    ...COMPONENTS
  ]
})
export class SharedModule { }
