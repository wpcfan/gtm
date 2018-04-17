package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 用户登录名未找到错误
 */
public final class LoginNotFoundException extends AbstractThrowableProblem {
    public LoginNotFoundException() {
        super(ErrorConstants.LOGIN_NOT_FOUND_TYPE, "登录名未找到", Status.BAD_REQUEST);
    }
}
