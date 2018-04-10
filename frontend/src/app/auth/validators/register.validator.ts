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
      return service.checkUniqueUsername(val).pipe(
        map(res => {
          return res ? { usernameNotUnique: true } : null;
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
      return service.checkUniqueEmail(val).pipe(
        map(res => {
          return res ? { emailNotUnique: true } : null;
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
      return service.checkUniqueMobile(val).pipe(
        map(res => {
          return res ? { mobileNotUnique: true } : null;
        })
      );
    };
  }
}
