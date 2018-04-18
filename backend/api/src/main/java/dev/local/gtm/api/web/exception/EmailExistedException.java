package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 用户 Email 已存在异常
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
public class EmailExistedException extends AbstractThrowableProblem {
    public EmailExistedException() {
        super(ErrorConstants.EMAIL_EXISTED_TYPE, "电子邮件已存在", Status.BAD_REQUEST);
    }
}
