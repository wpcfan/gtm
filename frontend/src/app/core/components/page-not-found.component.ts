import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

@Component({
  template: `
  <div fxLayout="row" fxLayoutAlign="center center">
    <mat-card>
      <mat-card-header>
        <mat-card-title> 又迷路了... </mat-card-title>
        <mat-card-subtitle> 404 - 没有找到页面 </mat-card-subtitle>
      </mat-card-header>
      <img mat-card-image [src]="notFoundImgSrc">
      <mat-card-actions>
        <button mat-button color="primary"> 回首页 </button>
      </mat-card-actions>
    </mat-card>
  </div>
  `,
  styles: [
    `
    :host {
      display: flex;
      flex: 1 1 auto;
    }
    mat-card {
      width: 70%;
    }
    `
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PageNotFoundComponent {
  notFoundImgSrc = 'assets/img/400_night_light.jpg';
}
