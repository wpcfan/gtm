package dev.local.gtm.api.service;

import dev.local.gtm.api.domain.Captcha;
import dev.local.gtm.api.service.dto.UserDTO;

public interface AuthService {
    void registerUser(UserDTO userDTO, String password);
    String login(String login, String password);
    void requestSmsCode(String mobile, String validateToken);
    String verifyMobile(String mobile, String code);
    Captcha requestCaptcha();
    String verifyCaptcha(String code, String token);
    void resetPassword(String key, String mobile, String password);
    boolean usernameExisted(String username);
    boolean emailExisted(String email);
    boolean mobileExisted(String mobile);
}
