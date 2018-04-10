import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

import { QuoteService } from '../../services/quote.service';
import { Quote } from '../../../domain/quote';
import { Auth } from '../../../domain/auth';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  quote$: Observable<Quote>;
  constructor(
    private quoteService: QuoteService,
    private authService: AuthService
  ) {
    this.quote$ = quoteService
      .getQuotes()
      .pipe(map(quotes => quotes[Math.floor(Math.random() * 10)]));
  }
  processLogin(auth: Auth) {
    this.authService
      .login(auth)
      .pipe(take(1))
      .subscribe(u => console.log(u));
  }
}
