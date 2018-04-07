import {
  Component,
  ChangeDetectionStrategy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { MenuGroup, IconType } from '../../domain/menu';

@Component({
  selector: 'app-sidebar',
  template: `
  <mat-nav-list>
    <ng-container *ngFor="let group of menuGroups">
      <h3 matSubheader> {{ group.name }} </h3>
      <ng-container *ngFor="let item of group.items">
        <mat-list-item
          [routerLink]="item.routerLink"
          (click)="menuClick(item.emitData)" [ngSwitch]="item.iconType">
          <mat-icon matListIcon *ngSwitchCase="iconType.SVG" [svgIcon]="item.iconName"></mat-icon>
          <mat-icon matListIcon *ngSwitchCase="iconType.FONT_AWESOME" [fontSet]="item?.fontSet" [fontIcon]="item.iconName"></mat-icon>
          <mat-icon matListIcon *ngSwitchDefault>{{ item.iconName }}</mat-icon>
          <h4 matLine> {{ item.title }} </h4>
          <p matLine> {{ item.subtitle }} </p>
        </mat-list-item>
      </ng-container>
      <mat-divider></mat-divider>
    </ng-container>
  </mat-nav-list>
  `,
  styles: [
    `
  .day-num {
    font-size: 48px;
    width: 48px;
    height: 48px;
  }
  `
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SidebarComponent {
  @Input() menuGroups: MenuGroup[] = [];
  @Output() menuClickEvent = new EventEmitter<any>();

  menuClick(data: any) {
    if (data) {
      this.menuClickEvent.emit(data);
    } else {
      this.menuClickEvent.emit();
    }
  }

  get iconType() {
    return IconType;
  }
}
