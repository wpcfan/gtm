package dev.local.gtm.api.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.domain.Auth;
import dev.local.gtm.api.domain.Captcha;
import dev.local.gtm.api.service.AuthService;
import dev.local.gtm.api.web.exception.InvalidPasswordException;
import dev.local.gtm.api.web.rest.vm.KeyAndPasswordVM;
import dev.local.gtm.api.web.rest.vm.UserVM;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.Serializable;

import static dev.local.gtm.api.repository.UserRepo.USERS_BY_EMAIL_CACHE;

/**
 * 用户鉴权资源接口
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthResource {

    private final AuthService authService;
    private final AppProperties appProperties;

    @ApiOperation(value = "用户登录鉴权接口",
            notes = "客户端在 RequestBody 中以 json 传入用户名、密码，如果成功以 json 形式返回该用户信息")
    @PostMapping(value = "/auth/login")
    public ResponseEntity<JWTToken> login(@RequestBody final Auth auth) {
        log.debug("REST 请求 -- 将对用户: {} 执行登录鉴权", auth);
        return generateJWTHeader(auth.getLogin(), auth.getPassword());
    }

    @PostMapping("/auth/register")
    public ResponseEntity<JWTToken> register(@Valid @RequestBody UserVM userVM) {
        log.debug("REST 请求 -- 注册用户: {} ", userVM);
        if (!checkPasswordLength(userVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        authService.registerUser(userVM, userVM.getPassword());
        return generateJWTHeader(userVM.getLogin(), userVM.getPassword());
    }

    @GetMapping(value = "/auth/mobile")
    public void requestSmsCode(@RequestParam String mobile, @RequestParam String token) {
        log.debug("REST 请求 -- 请求为手机号 {} 发送验证码，Captcha 验证 token 为 {} ", mobile, token);
        authService.requestSmsCode(mobile, token);
    }

    @PostMapping(value = "/auth/mobile")
    public ResetKey verifyMobile(@RequestBody MobileVerification verification) {
        log.debug("REST 请求 -- 验证手机号 {} 和短信验证码 {}", verification.getMobile(), verification.getCode());
        val key = authService.verifyMobile(verification.getMobile(), verification.getCode());
        return new ResetKey(key);
    }

    @PostMapping(value = "/auth/reset")
    public void resetPassword(@RequestBody KeyAndPasswordVM keyAndPasswordVM) {
        log.debug("REST 请求 -- 重置密码 {}", keyAndPasswordVM);
        authService.resetPassword(keyAndPasswordVM.getResetKey(), keyAndPasswordVM.getMobile(), keyAndPasswordVM.getPassword());
    }

    @GetMapping(value = "/auth/captcha")
    public Captcha requestCaptcha() {
        log.debug("REST 请求 -- 请求发送图形验证码 Captcha");
        return authService.requestCaptcha();
    }

    @PostMapping("/auth/captcha")
    public CaptchaResult verifyCaptcha(@RequestBody final CaptchaVerification verification) {
        log.debug("REST 请求 -- 验证 Captcha {}", verification);
        val result = authService.verifyCaptcha(verification.getCode(), verification.getToken());
        log.debug("Captcha 验证返回结果 {}", verification);
        return new CaptchaResult(result);
    }

    @GetMapping("/auth/search/username")
    public ExistCheck usernameExisted(
            @ApiParam(value = "用户名") @RequestParam("username") String username) {
        log.debug("REST 请求 -- 用户名是否存在 {}", username);
        return new ExistCheck(authService.usernameExisted(username));
    }

    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    @GetMapping("/auth/search/email")
    public ExistCheck emailExisted(@RequestParam("email") String email) {
        log.debug("REST 请求 -- email 是否存在 {}", email);
        return new ExistCheck(authService.emailExisted(email));
    }

    @GetMapping("/auth/search/mobile")
    public ExistCheck mobileExisted(@RequestParam("mobile") String mobile) {
        log.debug("REST 请求 -- email 是否存在 {}", mobile);
        return new ExistCheck(authService.mobileExisted(mobile));
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
                password.length() >= UserVM.PASSWORD_MIN_LENGTH &&
                password.length() <= UserVM.PASSWORD_MAX_LENGTH;
    }

    private ResponseEntity<JWTToken> generateJWTHeader(String login, String password) {
        val jwt = authService.login(login, password);
        val headers = new HttpHeaders();
        headers.add(
                appProperties.getSecurity().getAuthorization().getHeader(),
                appProperties.getSecurity().getJwt().getTokenPrefix() + jwt);
        log.debug("JWT token {} 加入到 HTTP 头", jwt);
        return new ResponseEntity<>(new JWTToken(jwt), headers, HttpStatus.OK);
    }

    /**
     * 简单返回 JWT token
     * 对于非常简单的需要封装成 JSON 的类，可以直接定义在 Controller 中
     */
    @Getter
    @Setter
    @AllArgsConstructor
    private static class JWTToken {
        @JsonProperty("id_token")
        private String idToken;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class CaptchaVerification {
        @JsonProperty("captcha_token")
        private String token;
        @JsonProperty("captcha_code")
        private String code;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MobileVerification {
        private String mobile;
        private String code;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class ResetKey {
        @JsonProperty("reset_key")
        private String resetKey;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class CaptchaResult {
        @JsonProperty("validate_token")
        private String validatedToken;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class ExistCheck implements Serializable {
        private static final long serialVersionUID = 1L;
        private boolean existed;
    }
}
