import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  Observable,
  Subject,
  Subscription,
  of,
  combineLatest,
  merge,
  BehaviorSubject,
  interval
} from 'rxjs';
import {
  map,
  take,
  switchMap,
  startWith,
  mergeMap,
  withLatestFrom,
  filter,
  catchError,
  tap,
  first,
  mapTo
} from 'rxjs/operators';

import { QuoteService } from '../../services/quote.service';
import { Quote } from '../../../domain/quote';
import { Auth } from '../../../domain/auth';
import { AuthService } from '../../services/auth.service';
import { Captcha } from '../../../domain/captcha';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnDestroy, OnInit {
  quote$: Observable<Quote>;
  captcha$: Observable<Captcha>;
  clickSub = new Subject();
  captchaSub = new BehaviorSubject<Captcha>(null);
  verifySub = new Subject<string>();
  sub = new Subscription();
  constructor(
    private quoteService: QuoteService,
    private authService: AuthService
  ) {
    this.quote$ = quoteService
      .getQuotes()
      .pipe(map(quotes => quotes[Math.floor(Math.random() * 10)]));
  }
  ngOnInit(): void {
    //Called after the constructor, initializing input properties, and the first call to ngOnChanges.
    //Add 'implements OnInit' to the class.
    this.captcha$ = this.clickSub.asObservable().pipe(
      startWith({}),
      switchMap(_ => this.authService.requestCaptcha()),
      tap(captcha => {
        this.captchaSub.next(captcha);
      })
    );

    this.sub = this.verifySub
      .pipe(
        withLatestFrom(this.captchaSub),
        switchMap(([code, captcha]) =>
          this.authService
            .verifyCaptcha(captcha.captcha_token, code)
            .pipe(
              map(res => res.validate_token),
              catchError(err => of(err.error.title))
            )
        )
      )
      .subscribe(t => console.log(t));
  }
  ngOnDestroy() {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
  processLogin(auth: Auth) {
    this.authService
      .login(auth)
      .pipe(take(1))
      .subscribe(u => console.log(u));
  }
  refreshCaptcha() {
    this.clickSub.next();
  }
  verifyCaptcha(code: string) {
    this.verifySub.next(code);
  }
}
