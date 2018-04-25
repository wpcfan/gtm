import {
  Component,
  OnInit,
  forwardRef,
  ChangeDetectionStrategy,
  EventEmitter,
  Output,
  Input,
  ViewChild,
  ElementRef,
  OnDestroy
} from '@angular/core';
import {
  NG_VALUE_ACCESSOR,
  NG_VALIDATORS,
  Validators,
  FormControl,
  ControlValueAccessor,
  FormBuilder,
  FormGroup
} from '@angular/forms';
import { Subscription } from 'rxjs';
import { debounceTime, filter } from 'rxjs/operators';
import { mobilePattern } from '../../../utils/regex';
import { mobileErrorMsg } from '../../../utils/validate-errors';

@Component({
  selector: 'app-verify-mobile',
  templateUrl: './verify-mobile.component.html',
  styleUrls: ['./verify-mobile.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => VerifyMobileComponent),
      multi: true
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => VerifyMobileComponent),
      multi: true
    }
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class VerifyMobileComponent
  implements ControlValueAccessor, OnInit, OnDestroy {
  @Input() mobilePlaceholder = '绑定手机号';
  @Input() codePlaceholder = '请输入短信验证码';
  @Input() mobile: string | null = null;
  @Output() requestCode = new EventEmitter<string>();
  @Output() mobileInputEvent = new EventEmitter<string>();
  form: FormGroup;
  private subs: Subscription[] = [];
  private propagateChange = (_: any) => {};

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.form = this.fb.group({
      regMobile: [
        { value: this.mobile, disabled: this.mobile },
        Validators.compose([
          Validators.required,
          Validators.pattern(mobilePattern)
        ])
      ],
      smsCode: [
        '',
        Validators.compose([Validators.required, Validators.pattern(/^\d{6}$/)])
      ]
    });
    if (!this.mobile) {
      const mobile = this.form.get('regMobile');
      this.subs.push(
        mobile.valueChanges
          .pipe(filter(_ => mobile.errors === null))
          .subscribe(val => this.mobileInputEvent.emit(val))
      );
    }
    const smsCode = this.form.get('smsCode');

    if (smsCode) {
      const code$ = smsCode.valueChanges;
      this.subs.push(
        code$.pipe(debounceTime(400)).subscribe(v =>
          this.propagateChange({
            mobile: this.mobile
              ? this.mobile
              : this.form.get('regMobile').value,
            code: v
          })
        )
      );
    }
  }
  ngOnDestroy(): void {
    this.subs.forEach(sub => {
      if (sub) {
        sub.unsubscribe();
      }
    });
    this.subs = [];
  }
  // 验证表单，验证结果正确返回 null 否则返回一个验证结果对象
  validate(c: FormControl): { [key: string]: any } | null {
    const val = c.value;
    if (!val) {
      return null;
    }
    const smsCode = this.form.get('smsCode');
    if (smsCode) {
      return smsCode.valid
        ? null
        : {
            smsCodeInvalid: true
          };
    }
    return {
      smsCodeInvalid: true
    };
  }
  writeValue(obj: any): void {
    this.mobile = obj;
  }
  registerOnChange(fn: any): void {
    this.propagateChange = fn;
  }
  registerOnTouched(fn: any): void {}

  get mobileErrors() {
    const mobile = this.form.get('regMobile');
    if (!mobile) {
      return '';
    }
    return mobileErrorMsg(mobile);
  }
  processClick() {
    this.requestCode.emit();
  }
}
