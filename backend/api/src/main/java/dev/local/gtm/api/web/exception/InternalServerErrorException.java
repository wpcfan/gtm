package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 服务器内部错误
 */
public class InternalServerErrorException extends AbstractThrowableProblem {

    public InternalServerErrorException(String message) {
        super(ErrorConstants.DEFAULT_TYPE, message, Status.INTERNAL_SERVER_ERROR);
    }
}
