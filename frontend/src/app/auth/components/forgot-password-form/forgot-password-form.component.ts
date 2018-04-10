import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {
  ValidatorFn,
  FormGroup,
  FormBuilder,
  Validators,
  FormControl
} from '@angular/forms';

@Component({
  selector: 'forgot-password-form',
  templateUrl: './forgot-password-form.component.html',
  styleUrls: ['./forgot-password-form.component.scss']
})
export class ForgotPasswordFormComponent implements OnInit {
  @Input() mobile: string | null = null;
  @Input() codeValidator: ValidatorFn;
  @Output() passwordEvent = new EventEmitter<string>();
  @Output() codeRequestEvent = new EventEmitter<string>();
  @Output() mobileInputEvent = new EventEmitter<string>();
  mobileForm: FormGroup;
  newPasswordForm: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnInit() {
    this.mobileForm = this.fb.group({
      oldCode: [this.mobile, Validators.required, this.codeValidator]
    });

    this.newPasswordForm = this.fb.group({
      password: [
        '',
        Validators.compose([Validators.required, Validators.minLength(8)])
      ],
      repeat: ['', [Validators.required, this.matchPassword('password')]]
    });
  }

  submit(form: FormGroup, ev: Event) {
    if (!form.valid || !form.value) {
      return;
    }
    if (form.value.password) {
      this.passwordEvent.emit(form.value.password);
    }
  }

  makeRequest(mobile: string) {
    this.codeRequestEvent.emit(mobile);
  }

  matchPassword(otherCtrlName: string) {
    let thisControl: FormControl;
    let otherControl: FormControl;
    return (control: FormControl) => {
      if (!control.parent) {
        return null;
      }
      // Initializing the validator.
      if (!thisControl) {
        thisControl = control;
        otherControl = control.parent.get(otherCtrlName) as FormControl;
        if (!otherControl) {
          throw new Error('matchPassword(): 未发现表单中有要比较的控件');
        }
        otherControl.valueChanges.subscribe(() => {
          thisControl.updateValueAndValidity();
        });
      }
      if (!otherControl) {
        return null;
      }
      if (otherControl.value !== thisControl.value) {
        return {
          matchOther: true
        };
      }
      return null;
    };
  }
  mobileInput(mobile: string) {
    this.mobileInputEvent.emit(mobile);
  }
}
