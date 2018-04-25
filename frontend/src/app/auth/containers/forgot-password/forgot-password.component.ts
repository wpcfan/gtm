import { Component, OnInit, Input } from '@angular/core';
import { AsyncValidatorFn } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { SmsValidator } from '../../validators/sms.validator';
import { take } from 'rxjs/operators';

@Component({
  selector: 'forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {
  loginBtnText = '登录';
  registerBtnText = '注册';
  codeValidator: AsyncValidatorFn;

  constructor(private service: AuthService) {
    this.codeValidator = SmsValidator.validateSmsCode(service);
  }

  ngOnInit() {}

  processCodeRequest(mobile: string) {
    this.service
      .requestSmsCode(mobile, '')
      .pipe(take(1))
      .subscribe(val => console.log(val));
  }

  processMobile(mobile: string) {
    console.log(mobile);
  }

  processPassword(password: string) {
    console.log(password);
  }
}
