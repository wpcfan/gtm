package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class OutgoingBadRequestException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    public OutgoingBadRequestException(String reason) {
        super(ErrorConstants.OUTGOING_BAD_REQUEST_TYPE, reason, Status.BAD_REQUEST);
    }
}
