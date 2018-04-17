package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class MobileExistedException extends AbstractThrowableProblem {
    public MobileExistedException() {
        super(ErrorConstants.MOBILE_EXISTED_TYPE, "手机号已存在", Status.BAD_REQUEST);
    }
}
