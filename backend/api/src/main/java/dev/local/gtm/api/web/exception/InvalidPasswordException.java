package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 密码错误
 */
public class InvalidPasswordException extends AbstractThrowableProblem {

    public InvalidPasswordException() {
        super(ErrorConstants.INVALID_PASSWORD_TYPE, "密码不正确", Status.BAD_REQUEST);
    }
}
