import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AsyncValidatorFn,
  AbstractControl
} from '@angular/forms';
import {
  usernamePattern,
  mobilePattern,
  emailPattern,
  humanNamePattern
} from '../../../utils/regex';
import * as _ from 'lodash';
import {
  usernameErrorMsg,
  emailErrorMsg,
  humanNameErrorMsg,
  mobileErrorMsg
} from '../../../utils/validate-errors';
import { User } from '../../../domain/user';

@Component({
  selector: 'register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss']
})
export class RegisterFormComponent implements OnInit {
  @Input() avatars: string[] = [];
  @Input() title = '注册';
  @Input() subtitle = '注册成为会员体验全部功能';
  @Input() loginBtnText = '已经注册？点击登录';
  @Input() forgotBtnText = '忘记密码？';
  @Input() usernameValidator: AsyncValidatorFn;
  @Input() emailValidator: AsyncValidatorFn;
  @Input() mobileValidator: AsyncValidatorFn;
  @Output() submitEvent = new EventEmitter();
  @Output() requestCaptcha = new EventEmitter();
  form: FormGroup;
  private readonly avatarName = 'avatars';
  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {
    //Called after the constructor, initializing input properties, and the first call to ngOnChanges.
    //Add 'implements OnInit' to the class.
    this.avatars = _.range(1, 16)
      .map(i => `${this.avatarName}:svg-${i}`)
      .reduce((r: string[], x: string) => [...r, x], []);
    this.form = this.fb.group({
      login: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(50),
          Validators.pattern(usernamePattern)
        ],
        this.usernameValidator
      ],
      mobile: [
        '',
        [Validators.required, Validators.pattern(mobilePattern)],
        this.mobileValidator
      ],
      email: [
        '',
        [Validators.required, Validators.pattern(emailPattern)],
        this.emailValidator
      ],
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(2),
          Validators.maxLength(50),
          Validators.pattern(humanNamePattern)
        ]
      ],
      avatar: [],
      passwords: this.fb.group(
        {
          password: [
            '',
            [
              Validators.required,
              Validators.minLength(8),
              Validators.maxLength(20)
            ]
          ],
          repeat: ['', Validators.required]
        },
        { validator: this.matchPassword }
      )
    });
  }

  matchPassword(c: AbstractControl) {
    const password = c.get('password').value;
    const repeat = c.get('repeat').value;
    if (password !== repeat) {
      c.get('repeat').setErrors({ notMatchPassword: true });
      return { notMatchPassword: true };
    } else {
      return null;
    }
  }

  submit({ valid, value }: FormGroup, ev: Event) {
    if (!valid) {
      return;
    }
    const user: User = {
      login: value.login,
      password: value.passwords.password,
      email: value.email,
      name: value.name,
      mobile: value.mobile,
      avatar: value.avatar
    };
    this.submitEvent.emit(user);
  }

  processCaptchaReq() {
    this.requestCaptcha.emit();
  }

  mobileInput(mobile: string) {}

  requestCode(mobile: string) {}

  get nameErrors() {
    const name = this.form.get('name');
    if (!name) {
      return '';
    }
    return humanNameErrorMsg(name);
  }

  get usernameErrors() {
    const username = this.form.get('username');
    if (!username) {
      return '';
    }
    return usernameErrorMsg(username);
  }

  get emailErrors() {
    const email = this.form.get('email');
    if (!email) {
      return '';
    }
    return emailErrorMsg(email);
  }

  get mobileErrors() {
    const mobile = this.form.get('mobile');
    if (!mobile) {
      return '';
    }
    return mobileErrorMsg(mobile);
  }

  get passwordErrors() {
    const password = this.form.get('passwords').get('password');
    if (!password) {
      return '';
    }
    return password.hasError('required')
      ? '密码为必填项'
      : password.hasError('minlength')
        ? `不能少于 ${password.errors['minlength'].requiredLength} 个字符`
        : password.hasError('maxlength')
          ? `不能超过 ${password.errors['maxlength'].requiredLength} 个字符`
          : '';
  }

  get repeatErrors() {
    const repeat = this.form.get('passwords').get('repeat');
    if (!repeat) {
      return '';
    }
    return repeat.hasError('required')
      ? '密码为必填项'
      : repeat.hasError('notMatchPassword')
        ? `密码不匹配`
        : '';
  }
}
