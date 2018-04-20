package dev.local.gtm.api.service;

import dev.local.gtm.api.service.dto.UserDTO;

public interface AuthService {
    void registerUser(UserDTO userDTO, String password);
    String login(String login, String password);
    String verifyMobile(String mobile, String code);
    String verifyCaptcha(String code, String token);
    void resetPassword(String key, String mobile, String password);
}
