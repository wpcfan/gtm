import { Component, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-footer',
  template: `
  <mat-toolbar color="primary">
    <span class="fill-remaining-space"></span>
    <span>&copy; 2018 版权所有: 接灰的电子产品</span>
    <span class="fill-remaining-space"></span>
  </mat-toolbar>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FooterComponent {}
