import {
  Component,
  OnInit,
  ChangeDetectionStrategy,
  EventEmitter,
  Output,
  Input
} from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { usernamePattern } from '../../../utils/regex';
import { Auth } from '../../../domain/auth';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class LoginFormComponent implements OnInit {
  form: FormGroup;
  @Input() title = '登录';
  @Input() subtitle = '使用您的用户名密码登录';
  @Input() regBtnText = '还没有注册？';
  @Input() forgotBtnText = '忘记密码？';
  @Output() submitEvent = new EventEmitter<Auth>();

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.form = this.fb.group({
      login: [
        '',
        [
          Validators.required,
          Validators.minLength(3),
          Validators.maxLength(50),
          Validators.pattern(usernamePattern)
        ]
      ],
      password: [
        '',
        [Validators.required, Validators.minLength(8), Validators.maxLength(20)]
      ]
    });
  }

  submit({ value, valid }: FormGroup, ev: Event) {
    if (!valid) {
      return;
    }
    this.submitEvent.emit(value);
  }
}
