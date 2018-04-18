package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ResetKeyNotMatchException extends AbstractThrowableProblem {
    public ResetKeyNotMatchException() {
        super(ErrorConstants.RESET_KEY_NOT_MATCH_TYPE, "重置密钥不匹配", Status.BAD_REQUEST);
    }
}
