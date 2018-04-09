import { AbstractControl } from '@angular/forms';

export const humanNameErrorMsg = (name: AbstractControl) => {
  return name.hasError('required')
    ? '姓名为必填项'
    : name.hasError('minlength')
      ? '不能少于 2 个字符'
      : name.hasError('maxlength')
        ? '最大不能超过 50 个字符'
        : name.hasError('pattern')
          ? '中文姓名只能由汉字构成；英文姓名由英文字母和空格构成'
          : '';
};

export const usernameErrorMsg = (username: AbstractControl) => {
  return username.hasError('required')
    ? '用户名为必填项'
    : username.hasError('minlength')
      ? `不能少于 ${username.errors['minlength'].requiredLength} 个字符`
      : username.hasError('maxlength')
        ? `不能超过 ${username.errors['maxlength'].requiredLength} 个字符`
        : username.hasError('pattern')
          ? '用户名只能由英文字母、数字和下划线组成'
          : username.hasError('usernameNotUnique')
            ? '已经存在相同的用户名'
            : '';
};

export const emailErrorMsg = (email: AbstractControl) => {
  return email.hasError('required')
    ? '电子邮件为必填项'
    : email.hasError('pattern')
      ? '不是一个合法的电子邮件'
      : email.hasError('emailNotUnique') ? '已经存在相同的电子邮件' : '';
};

export const mobileErrorMsg = (mobile: AbstractControl) => {
  return mobile.hasError('required')
    ? '手机为必填项'
    : mobile.hasError('pattern')
      ? '手机号应为11位数字'
      : mobile.hasError('mobileNotUnique') ? '已经存在相同的手机号' : '';
};
