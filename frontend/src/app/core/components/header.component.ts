import {
  Component,
  ChangeDetectionStrategy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

@Component({
  selector: 'app-header',
  template: `
  <mat-toolbar color="primary">
    <button mat-icon-button (click)="toggleMenu()" *ngIf="!hideForGuest">
      <mat-icon>menu</mat-icon>
    </button>
    <span>{{ title }}</span>
    <span class="fill-remaining-space"></span>
    <mat-slide-toggle (change)="toggleDarkMode($event.checked)" *ngIf="!hideForGuest">黑夜模式</mat-slide-toggle>
    <button *ngIf="!hideForGuest" mat-button (click)="handleLogout()">退出</button>
  </mat-toolbar>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeaderComponent {
  @Input() title = '企业协作平台';
  @Input() hideForGuest = false;
  @Output() toggleMenuEvent = new EventEmitter<void>();
  @Output() toggleDarkModeEvent = new EventEmitter<boolean>();
  @Output() logoutEvent = new EventEmitter();

  toggleMenu() {
    this.toggleMenuEvent.emit();
  }

  handleLogout() {
    this.logoutEvent.emit();
  }

  toggleDarkMode(checked: boolean) {
    this.toggleDarkModeEvent.emit(checked);
  }
}
