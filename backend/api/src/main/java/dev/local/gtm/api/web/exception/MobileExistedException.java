package dev.local.gtm.api.web.exception;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

/**
 * 用户手机号已存在异常
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
public class MobileExistedException extends AbstractThrowableProblem {
    private static final long serialVersionUID = 1L;

    public MobileExistedException() {
        super(ErrorConstants.MOBILE_EXISTED_TYPE, "手机号已存在", Status.BAD_REQUEST);
    }
}
