package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 登录名已存在异常
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
public class LoginExistedException extends AbstractThrowableProblem {
    public LoginExistedException() {
        super(ErrorConstants.LOGIN_EXISTED_TYPE, "登录名已存在", Status.BAD_REQUEST);
    }
}
