package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class EmailExistedException extends AbstractThrowableProblem {
    public EmailExistedException() {
        super(ErrorConstants.EMAIL_EXISTED_TYPE, "电子邮件已存在", Status.BAD_REQUEST);
    }
}
