import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { pluck } from 'rxjs/operators';
import { AuthModule } from '../auth.module';
import { Auth } from '../../domain/auth';
import { User } from '../../domain/user';
import { environment } from '../../../environments/environment';
import { Captcha } from '../../domain/captcha';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private headers = new HttpHeaders().append(
    'Content-Type',
    'application/json'
  );
  constructor(private http: HttpClient) {}
  /**
   * 用于用户的登录鉴权
   * @param auth 用户的登录信息，一般是登录名（目前是用户名，以后会允许手机号）和密码
   */
  login(auth: Auth): Observable<string> {
    return this.http
      .post<{ id_token: string }>(
        `${environment.apiBaseUrl}auth/login`,
        JSON.stringify(auth),
        { headers: this.headers }
      )
      .pipe(pluck('id_token'));
  }
  /**
   * 用于用户的注册
   * @param user 用户注册信息
   */
  register(user: User): Observable<string> {
    return this.http
      .post<{ id_token: string }>(
        `${environment.apiBaseUrl}auth/register`,
        JSON.stringify(user),
        { headers: this.headers }
      )
      .pipe(pluck('id_token'));
  }
  /**
   * 请求发送短信验证码到待验证手机，成功返回空对象 {}
   * @param mobile 待验证的手机号
   */
  requestSmsCode(mobile: string, token: string): Observable<void> {
    const params = new HttpParams()
      .append('mobile', mobile)
      .append('token', token);
    return this.http.get<void>(`${environment.apiBaseUrl}auth/mobile`, {
      headers: this.headers,
      params: params
    });
  }
  /**
   * 验证手机号和短信验证码是否匹配
   * @param mobile 待验证手机号
   * @param code 收到的短信验证码
   */
  verifySmsCode(mobile: string, code: string): Observable<void> {
    return this.http.post<void>(
      `${environment.apiBaseUrl}auth/mobile`,
      JSON.stringify({ mobile: mobile, code: code }),
      { headers: this.headers }
    );
  }
  /**
   * 请求发送短信验证码到待验证手机，成功返回空对象 {}
   * @param mobile 待验证的手机号
   */
  requestCaptcha() {
    return this.http.get<Captcha>(`${environment.apiBaseUrl}auth/captcha`, {
      headers: this.headers
    });
  }
  /**
   * 验证手机号和短信验证码是否匹配
   * @param mobile 待验证手机号
   * @param code 收到的短信验证码
   */
  verifyCaptcha(token: string, code: string) {
    return this.http.post<{ validate_token: string }>(
      `${environment.apiBaseUrl}auth/captcha`,
      JSON.stringify({ captcha_token: token, captcha_code: code }),
      {
        headers: this.headers
      }
    );
  }
  /**
   * 检查用户名是否唯一
   * @param username 用户名
   */
  usernameExisted(username: string) {
    const params = new HttpParams().append('username', username);
    return this.http.get<{ existed: boolean }>(
      `${environment.apiBaseUrl}auth/search/username`,
      {
        headers: this.headers,
        params: params
      }
    );
  }
  /**
   * 检查电子邮件是否唯一
   * @param email 电子邮件
   */
  emailExisted(email: string) {
    const params = new HttpParams().append('email', email);
    return this.http.get<{ existed: boolean }>(
      `${environment.apiBaseUrl}auth/search/email`,
      {
        headers: this.headers,
        params: params
      }
    );
  }
  /**
   * 检查手机号是否唯一
   * @param mobile 手机号
   */
  mobileExisted(mobile: string) {
    const params = new HttpParams().append('mobile', mobile);
    return this.http.get<{ existed: boolean }>(
      `${environment.apiBaseUrl}auth/search/mobile`,
      {
        headers: this.headers,
        params: params
      }
    );
  }
}
