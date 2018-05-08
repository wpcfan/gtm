package dev.local.gtm.api.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 用户尚未激活异常
 */
public class UserNotActivatedException extends AuthenticationException {
    private static final long serialVersionUID = 1L;

    public UserNotActivatedException(String message) {
        super(message);
    }

    public UserNotActivatedException(String message, Throwable t) {
        super(message, t);
    }
}
