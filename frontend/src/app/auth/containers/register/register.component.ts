import { Component, OnInit } from '@angular/core';
import { AsyncValidatorFn } from '@angular/forms';
import { take } from 'rxjs/operators';
import { Router } from '@angular/router';
import { User } from '../../../domain/user';
import { AuthService } from '../../services/auth.service';
import { RegisterValidator } from '../../validators/register.validator';

@Component({
  selector: 'register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  usernameValidator: AsyncValidatorFn;
  emailValidator: AsyncValidatorFn;
  mobileValidator: AsyncValidatorFn;
  constructor(private service: AuthService, private router: Router) {
    this.usernameValidator = RegisterValidator.validateUniqueUsername(service);
    this.mobileValidator = RegisterValidator.validateUniqueMobile(service);
    this.emailValidator = RegisterValidator.validateUniqueEmail(service);
  }

  ngOnInit() {}

  processRegister(user: User) {
    this.service
      .register(user)
      .pipe(take(1))
      .subscribe(u => {
        console.log(u);
      });
  }
}
