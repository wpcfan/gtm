package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 用户登录名未找到异常
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
public final class LoginNotFoundException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    public LoginNotFoundException() {
        super(ErrorConstants.LOGIN_NOT_FOUND_TYPE, "登录名未找到", Status.BAD_REQUEST);
    }
}
