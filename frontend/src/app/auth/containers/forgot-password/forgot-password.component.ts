import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {
  @Input() loginBtnText = '登录';
  @Input() registerBtnText = '注册';
  constructor() {}

  ngOnInit() {}

  processCodeRequest() {}
}
