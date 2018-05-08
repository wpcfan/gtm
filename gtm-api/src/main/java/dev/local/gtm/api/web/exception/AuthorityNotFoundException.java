package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class AuthorityNotFoundException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    public AuthorityNotFoundException() {
        super(ErrorConstants.AUTHORITY_NOT_FOUND_TYPE, "角色未找到", Status.BAD_REQUEST);
    }
}
