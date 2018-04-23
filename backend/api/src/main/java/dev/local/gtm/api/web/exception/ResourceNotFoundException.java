package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class ResourceNotFoundException extends AbstractThrowableProblem {
    public ResourceNotFoundException(String message) {
        super(ErrorConstants.RESOURCE_NOT_FOUND_TYPE, message, Status.BAD_REQUEST);
    }
}
