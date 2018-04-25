import {
  Component,
  OnInit,
  ViewChild,
  ElementRef,
  EventEmitter,
  Output,
  Input
} from '@angular/core';
import { Observable, fromEvent, interval } from 'rxjs';
import {
  switchMap,
  map,
  takeWhile,
  startWith,
  tap,
  filter
} from 'rxjs/operators';

@Component({
  selector: 'app-count-down-button',
  template: `
  <button mat-button #veriBtn [disabled]="inactive">
    {{ btnLabel$ | async }}
  </button>
  `,
  styles: [``]
})
export class CountDownButtonComponent implements OnInit {
  @Input() countdown = 60;
  @Input() initialLabel = '发送';
  @Input() retryLabel = '再次发送';
  @Input() inactive = false;
  @Output() countBtnClick = new EventEmitter<void>();
  @ViewChild('veriBtn', { read: ElementRef })
  veriBtn: ElementRef;
  btnLabel$: Observable<string>;
  constructor() {}

  ngOnInit() {
    const countDown$ = interval(1000).pipe(
      map(i => this.countdown - i),
      takeWhile(v => v >= 0),
      startWith(this.countdown)
    );
    this.btnLabel$ = fromEvent(this.veriBtn.nativeElement, 'click').pipe(
      filter(_ => !this.inactive),
      tap(_ => {
        console.log('click: ');
        this.countBtnClick.emit();
        this.inactive = true;
      }),
      switchMap(_ => countDown$),
      map(i => {
        if (i === 0) {
          this.inactive = false;
        }
        return i > 0 ? `还剩 ${i} 秒` : this.retryLabel;
      }),
      startWith(this.initialLabel)
    );
  }
}
