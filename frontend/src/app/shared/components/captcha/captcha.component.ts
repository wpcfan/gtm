import {
  Component,
  OnInit,
  Input,
  ChangeDetectionStrategy,
  forwardRef,
  Output,
  EventEmitter,
  ViewChild,
  ElementRef,
  OnDestroy,
  HostBinding,
  Optional,
  Self
} from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  ControlValueAccessor,
  NG_VALUE_ACCESSOR,
  NgControl
} from '@angular/forms';
import { fromEvent, Subscription, Subject } from 'rxjs';
import { tap, debounceTime } from 'rxjs/operators';
import { MatFormFieldControl } from '@angular/material';
import { FocusMonitor } from '@angular/cdk/a11y';
import { coerceBooleanProperty } from '@angular/cdk/coercion';

@Component({
  selector: 'app-captcha',
  templateUrl: './captcha.component.html',
  styleUrls: ['./captcha.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [{ provide: MatFormFieldControl, useExisting: CaptchaComponent }]
})
export class CaptchaComponent
  implements
    OnInit,
    OnDestroy,
    ControlValueAccessor,
    MatFormFieldControl<string> {
  static nextId = 0;
  @HostBinding() id = `app-captcha-${CaptchaComponent.nextId++}`;
  @HostBinding('attr.aria-describedby') describedBy = '';
  @Input() captchaUrl = '';
  @Input() debounceTime = 300;
  @Output() requestCaptcha = new EventEmitter<void>();
  @ViewChild('captchaImg', { read: ElementRef })
  captchaImg: ElementRef;
  form: FormGroup;
  stateChanges = new Subject<void>();
  focused = false;
  errorState = false;
  controlType = 'app-captcha';
  autofilled = false;
  private subs: Subscription[] = [];
  private propagateChange = (_: any) => {};
  private _placeholder = '请输入图片中的数字';
  private _required = false;
  private _disabled = false;

  constructor(
    private fb: FormBuilder,
    private fm: FocusMonitor,
    private elRef: ElementRef,
    @Optional()
    @Self()
    public ngControl: NgControl
  ) {
    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }
    fm.monitor(elRef.nativeElement, true).subscribe(origin => {
      this.focused = !!origin;
      this.stateChanges.next();
    });
  }

  ngOnInit() {
    this.form = this.fb.group({ code: [] });
    const code = this.form.get('code');
    if (!code) {
      return;
    }
    this.subs.push(
      code.valueChanges
        .pipe(debounceTime(this.debounceTime))
        .subscribe(val => this.propagateChange(val))
    );
    this.subs.push(
      fromEvent(this.captchaImg.nativeElement, 'click')
        .pipe(debounceTime(this.debounceTime))
        .subscribe(_ => this.requestCaptcha.emit())
    );
  }

  ngOnDestroy() {
    this.stateChanges.complete();
    this.fm.stopMonitoring(this.elRef.nativeElement);
    this.subs.forEach(sub => {
      if (sub) {
        sub.unsubscribe();
      }
    });
    this.subs = [];
  }

  setDescribedByIds(ids: string[]) {
    this.describedBy = ids.join(' ');
  }
  onContainerClick(event: MouseEvent) {
    if ((event.target as Element).tagName.toLowerCase() != 'input') {
      this.elRef.nativeElement.querySelector('input').focus();
    }
  }

  @Input()
  get disabled() {
    return this._disabled;
  }
  set disabled(dis) {
    this._disabled = coerceBooleanProperty(dis);
    this.stateChanges.next();
  }

  @Input()
  get required() {
    return this._required;
  }
  set required(req) {
    this._required = coerceBooleanProperty(req);
    this.stateChanges.next();
  }

  @HostBinding('class.floating')
  get shouldLabelFloat() {
    return this.focused || !this.empty;
  }

  get empty() {
    const n = this.form.value;
    return !n.code;
  }

  @Input()
  get value(): string | null {
    const n = this.form.value;
    if (n.code.length === 4) {
      return n.code;
    }
    return null;
  }
  set value(captcha: string | null) {
    const val = captcha || '';
    this.form.setValue({
      code: val
    });
    this.stateChanges.next();
  }

  @Input()
  get placeholder() {
    return this._placeholder;
  }
  set placeholder(plh) {
    this._placeholder = plh;
    this.stateChanges.next();
  }

  writeValue(obj: any): void {
    this.form.patchValue({ code: obj });
  }
  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }
  registerOnTouched(fn: any): void {}
  setDisabledState?(isDisabled: boolean): void {}
}
