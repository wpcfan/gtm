package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.config.propsupport.SmsCaptchaProperties;
import dev.local.gtm.api.config.propsupport.SmsCodeProperties;
import dev.local.gtm.api.domain.*;
import dev.local.gtm.api.repository.UserRepo;
import dev.local.gtm.api.util.CredentialUtil;
import dev.local.gtm.api.web.exception.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

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

    private final UserRepo userRepo;
    private final RestTemplate restTemplate;
    private final SmsCaptchaProperties captchaProperties;
    private final SmsCodeProperties codeProperties;

    @ApiOperation(value = "用户登录鉴权接口",
            notes = "客户端在 RequestBody 中以 json 传入用户名、密码，如果成功以 json 形式返回该用户信息")
    @PostMapping(value = "/auth/login")
    public User login(@RequestBody final Auth auth) {
        log.debug("REST 请求 -- 将对用户: {} 执行登录鉴权", auth);
        return userRepo.findOneByLogin(auth.getLogin())
                .map(user -> {
                    if (user.getPassword().equals(auth.getPassword())) {
                        throw new InvalidPasswordException();
                    }
                    return user;
                }).orElseThrow(LoginNotFoundException::new);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        log.debug("REST 请求 -- 注册用户: {} ", user);
        if (userRepo.findOneByLogin(user.getLogin()).isPresent()) {
            throw new LoginExistedException();
        }
        if (userRepo.findOneByMobile(user.getMobile()).isPresent()) {
            throw new MobileExistedException();
        }
        if (userRepo.findOneByEmail(user.getEmail()).isPresent()) {
            throw new EmailExistedException();
        }
        return ResponseEntity.ok(userRepo.save(user));
    }

    @PostMapping(value = "/auth/mobile")
    @ResponseStatus(value = HttpStatus.OK)
    public void verifyMobile(@RequestBody MobileVerification verification) {
        log.debug("REST 请求 -- 验证手机号 {} 和短信验证码 {}", verification.getMobile(), verification.getCode());
        userRepo.findOneByMobile(verification.getMobile())
                .map(user -> {
                    val code = verifySmsCode(verification);
                    if (code.value() != 200) {
                        throw new MobileVerificationFailedException(code.getReasonPhrase());
                    }
                    user.setResetKey(CredentialUtil.generateResetKey());
                    return userRepo.save(user);
                })
                .orElseThrow(MobileNotFoundException::new);
    }

    @PostMapping(value = "/auth/reset")
    public void resetPassword(@RequestBody KeyAndPassword keyAndPassword) {
        log.debug("REST 请求 -- 重置密码 {}", keyAndPassword);
        userRepo.findOneByMobile(keyAndPassword.getMobile())
                .map(user -> {
                    if (!user.getResetKey().equals(keyAndPassword.getResetKey())) {
                        throw new ResetKeyNotMatchException();
                    }
                    user.setPassword(keyAndPassword.getPassword());
                    user.setResetKey(null);
                    return userRepo.save(user);
                })
                .orElseThrow(LoginNotFoundException::new);
    }

    @PostMapping("/auth/captcha")
    public Captcha verifyCaptcha(@RequestBody final Captcha captcha) {
        val body = new HashMap<String, String>();
        body.put("captcha_code", captcha.getCode());
        body.put("captcha_token", captcha.getToken());
        val entity = new HttpEntity<>(body);
        try {
            val validateCaptcha = restTemplate.postForObject(captchaProperties.getVerificationUrl(), entity, Captcha.class);
            if (validateCaptcha == null) {
                throw new InternalServerErrorException("返回对象为空，无法进行验证");
            }
            return Captcha.builder()
                    .code(captcha.getCode())
                    .token(captcha.getToken())
                    .validatedMsg(validateCaptcha.getValidatedMsg())
                    .build();
        } catch (HttpStatusCodeException ex) {
            throw new CaptchaVerificationFailedException(ex.getStatusCode().getReasonPhrase());
        }
    }

    private HttpStatus verifySmsCode(final MobileVerification verification) {
        val body = new HashMap<String, String>();
        body.put("mobilePhoneNumber", verification.getMobile());
        val entity = new HttpEntity<>(body);
        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    codeProperties.getVerificationUrl()+"/"+verification.getCode(),
                    entity, Void.class);
            return response.getStatusCode();
        } catch (HttpStatusCodeException ex) {
            return ex.getStatusCode();
        } catch (RestClientException ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
