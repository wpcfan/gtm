import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { AuthModule } from '../auth.module';
import { Auth } from '../../domain/auth';
import { User } from '../../domain/user';

@Injectable()
export class AuthService {
  constructor() {}
  /**
   * 用于用户的登录鉴权
   * @param auth 用户的登录信息，一般是登录名（目前是用户名，以后会允许手机号）和密码
   */
  login(auth: Auth): Observable<User> {
    const user: User = {
      username: 'test',
      mobile: '13012341234',
      email: 'zhangsan@local.dev',
      name: 'Zhang San',
      avatar: 'assets/img/avatar/001.svg',
      roles: []
    };
    return of(user);
  }
  /**
   * 用于用户的注册
   * @param user 用户注册信息
   */
  register(user: User): Observable<User> {
    const user_add: User = { ...user, id: '123abc' };
    return of(user_add);
  }
  /**
   * 请求发送短信验证码到待验证手机，成功返回空对象 {}
   * @param mobile 待验证的手机号
   */
  requestSmsCode(mobile: string) {
    return of({});
  }
  /**
   * 验证手机号和短信验证码是否匹配
   * @param mobile 待验证手机号
   * @param code 收到的短信验证码
   */
  verifySmsCode(mobile: string, code: string) {
    return of(mobile === '13012341234' && code === '123456');
  }
  /**
   * 检查用户名是否唯一
   * @param username 用户名
   */
  checkUniqueUsername(username: string) {
    return of(username === 'lisi');
  }
  /**
   * 检查电子邮件是否唯一
   * @param email 电子邮件
   */
  checkUniqueEmail(email: string) {
    return of(email === 'lisi@local.dev');
  }
  /**
   * 检查手机号是否唯一
   * @param mobile 手机号
   */
  checkUniqueMobile(mobile: string) {
    return of(mobile === '13112341234');
  }
}
