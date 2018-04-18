package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class MobileVerificationFailedException extends AbstractThrowableProblem {
    public MobileVerificationFailedException(String reason) {
        super(ErrorConstants.MOBILE_VERIFICATION_FAILED_TYPE, reason, Status.BAD_REQUEST);
    }
}
