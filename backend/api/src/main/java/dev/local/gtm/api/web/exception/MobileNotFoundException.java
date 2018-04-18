package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class MobileNotFoundException extends AbstractThrowableProblem {
    public MobileNotFoundException() {
        super(ErrorConstants.MOBILE_NOT_FOUND_TYPE, "手机号不存在", Status.BAD_REQUEST);
    }
}
