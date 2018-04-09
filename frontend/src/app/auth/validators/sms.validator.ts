import { AbstractControl } from '@angular/forms';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export class SmsValidator {
  static validateSmsCode(service: AuthService) {
    return (control: AbstractControl) => {
      const mobile = control.get('mobile');
      const code = control.get('code');
      if (!mobile || !code) {
        throw new Error('SmsValidator: 没有找到手机号或验证码');
      }
      return service.verifySmsCode(mobile.value, code.value).pipe(
        map(res => {
          return res ? null : { codeInvalid: true };
        })
      );
    };
  }
}
