import { NgModule } from '@angular/core';
import { AuthRoutingModule } from './auth-routing.module';
import { SharedModule } from '../shared/shared.module';
import { QuoteService } from './services/quote.service';
import { AuthService } from './services/auth.service';
import { AuthGuardService } from './services/auth-guard.service';
import { LoginFormComponent } from './components/login-form/login-form.component';
import { RegisterFormComponent } from './components/register-form/register-form.component';
import { ForgotPasswordFormComponent } from './components/forgot-password-form/forgot-password-form.component';
import { LoginComponent } from './containers/login/login.component';
import { RegisterComponent } from './containers/register/register.component';
import { ForgotPasswordComponent } from './containers/forgot-password/forgot-password.component';
import { QuoteComponent } from './components/quote/quote.component';

@NgModule({
  imports: [SharedModule, AuthRoutingModule],
  declarations: [
    LoginFormComponent,
    RegisterFormComponent,
    ForgotPasswordFormComponent,
    LoginComponent,
    RegisterComponent,
    ForgotPasswordComponent,
    QuoteComponent
  ],
  providers: [QuoteService, AuthService, AuthGuardService]
})
export class AuthModule {}
