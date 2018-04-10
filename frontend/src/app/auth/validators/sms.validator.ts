import { AbstractControl } from '@angular/forms';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export class SmsValidator {
  static validateSmsCode(service: AuthService) {
    return (control: AbstractControl) => {
      const val = control.value;
      if (!val.mobile || !val.code) {
        throw new Error('SmsValidator: 没有找到手机号或验证码');
      }
      return service.verifySmsCode(val.mobile, val.code).pipe(
        map(res => {
          return res ? null : { codeInvalid: true };
        })
      );
    };
  }
}
