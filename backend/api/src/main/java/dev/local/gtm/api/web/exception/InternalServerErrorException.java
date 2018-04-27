package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 服务器内部错误
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
public class InternalServerErrorException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    public InternalServerErrorException(String message) {
        super(ErrorConstants.DEFAULT_TYPE, message, Status.INTERNAL_SERVER_ERROR);
    }
}
