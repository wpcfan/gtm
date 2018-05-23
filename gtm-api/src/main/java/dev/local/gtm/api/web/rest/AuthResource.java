package dev.local.gtm.api.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.domain.Auth;
import dev.local.gtm.api.domain.Captcha;
import dev.local.gtm.api.domain.JWTToken;
import dev.local.gtm.api.service.AuthService;
import dev.local.gtm.api.web.exception.InvalidPasswordException;
import dev.local.gtm.api.web.rest.vm.KeyAndPasswordVM;
import dev.local.gtm.api.web.rest.vm.UserVM;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * 用户鉴权资源接口
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthResource {

    private final AuthService authService;
    private final AppProperties appProperties;

    @ApiOperation(value = "用户登录鉴权接口", notes = "客户端在 RequestBody 中以 json 传入用户名、密码，如果成功以 json 形式返回该用户信息")
    @PostMapping("/auth/login")
    public ResponseEntity<JWTToken> login(@Valid @RequestBody final Auth auth) {
        log.debug("REST 请求 -- 将对用户: {} 执行登录鉴权", auth);
        authService.verifyCaptchaToken(auth.getValidateToken());
        return generateJWTHeader(auth.getLogin(), auth.getPassword());
    }

    @PostMapping("/auth/register")
    public ResponseEntity<JWTToken> register(@Valid @RequestBody final UserVM userVM) {
        log.debug("REST 请求 -- 注册用户: {} ", userVM);
        if (!checkPasswordLength(userVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        authService.verifyCaptchaToken(userVM.getValidateToken());
        authService.registerUser(userVM.toUserDTO(), userVM.getPassword());
        return generateJWTHeader(userVM.getLogin(), userVM.getPassword());
    }

    @PostMapping("/auth/token")
    public JWTToken refreshAndGetAuthenticationToken(@RequestBody final RefreshToken token) {
        log.debug("更新 token，客户端提交的 refreshToke 为 {}", token);
        return authService.refreshToken(token.refreshToken);
    }

    @GetMapping("/auth/mobile")
    public void requestSmsCode(@RequestParam final String mobile, @RequestParam final String token) {
        log.debug("REST 请求 -- 请求为手机号 {} 发送验证码，Captcha 验证 token 为 {} ", mobile, token);
        authService.requestSmsCode(mobile, token);
    }

    @PostMapping("/auth/usermobile")
    public ResetKey verifyUserMobile(@Valid @RequestBody final MobileVerification verification) {
        log.debug("REST 请求 -- 验证手机号 {} 和短信验证码 {}", verification.getMobile(), verification.getCode());

        val key = authService.verifyUserMobile(verification.getMobile(), verification.getCode());
        return new ResetKey(key);
    }

    @PostMapping("/auth/mobile")
    public void verifyMobile(@Valid @RequestBody final MobileVerification verification) {
        log.debug("REST 请求 -- 验证手机号 {} 和短信验证码 {}", verification.getMobile(), verification.getCode());

        authService.verifyMobile(verification.getMobile(), verification.getCode());
    }

    @PostMapping("/auth/reset")
    public void resetPassword(@Valid @RequestBody final KeyAndPasswordVM keyAndPasswordVM) {
        log.debug("REST 请求 -- 重置密码 {}", keyAndPasswordVM);
        authService.resetPassword(keyAndPasswordVM.getResetKey(), keyAndPasswordVM.getMobile(),
                keyAndPasswordVM.getPassword());
    }

    @GetMapping("/auth/captcha")
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
    public ExistCheck usernameExisted(@ApiParam(value = "用户名") @RequestParam("username") String username) {
        log.debug("REST 请求 -- 用户名是否存在 {}", username);
        return new ExistCheck(authService.usernameExisted(username));
    }

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
        return !StringUtils.isEmpty(password) && password.length() >= UserVM.PASSWORD_MIN_LENGTH
                && password.length() <= UserVM.PASSWORD_MAX_LENGTH;
    }

    private ResponseEntity<JWTToken> generateJWTHeader(String login, String password) {
        val jwt = authService.login(login, password);
        val headers = new HttpHeaders();
        headers.add(appProperties.getSecurity().getAuthorization().getHeader(),
                appProperties.getSecurity().getJwt().getTokenPrefix() + jwt);
        log.debug("JWT token {} 加入到 HTTP 头", jwt);
        return new ResponseEntity<>(jwt, headers, HttpStatus.OK);
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaptchaVerification {
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
    public static class ResetKey {
        @JsonProperty("reset_key")
        private String resetKey;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CaptchaResult {
        @JsonProperty("validate_token")
        private String validatedToken;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshToken {
        @JsonProperty("refresh_token")
        private String refreshToken;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ExistCheck implements Serializable {
        private static final long serialVersionUID = 1L;
        private boolean existed;
    }
}
