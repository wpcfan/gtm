import { AbstractControl } from '@angular/forms';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';

export class RegisterValidator {
  static validateUniqueUsername(service: AuthService) {
    return (control: AbstractControl) => {
      const val = control.value;
      if (!val) {
        return of(null);
      }
      return service.usernameExisted(val).pipe(
        map(res => {
          return res.existed ? { usernameNotUnique: true } : null;
        })
      );
    };
  }
  static validateUniqueEmail(service: AuthService) {
    return (control: AbstractControl) => {
      const val = control.value;
      if (!val) {
        return of(null);
      }
      return service.emailExisted(val).pipe(
        map(res => {
          return res.existed ? { emailNotUnique: true } : null;
        })
      );
    };
  }
  static validateUniqueMobile(service: AuthService) {
    return (control: AbstractControl) => {
      const val = control.value;
      if (!val) {
        return of(null);
      }
      return service.mobileExisted(val).pipe(
        map(res => {
          return res.existed ? { mobileNotUnique: true } : null;
        })
      );
    };
  }
}
