package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.config.propsupport.SmsCaptchaProperties;
import dev.local.gtm.api.config.propsupport.SmsCodeProperties;
import dev.local.gtm.api.domain.Auth;
import dev.local.gtm.api.domain.Captcha;
import dev.local.gtm.api.domain.MobileVerification;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.repository.UserRepo;
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
        val user = userRepo.findOneByLogin(auth.getLogin());
        if (!user.isPresent()) {
            throw new LoginNotFoundException();
        }
        if (!user.get().getPassword().equals(auth.getPassword())) {
            throw new InvalidPasswordException();
        }
        return user.get();
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
        if (!userRepo.findOneByMobile(verification.getMobile()).isPresent()) {
            throw new MobileNotFoundException();
        }
        val code = verifySmsCode(verification);
        if (code.value() != 200) {
            throw new MobileVerificationFailedException(code.getReasonPhrase());
        }
    }

    @PostMapping(value = "/auth/reset")
    @ResponseStatus(value = HttpStatus.OK)
    public void resetPassword(@RequestBody Auth auth) {
        log.debug("REST 请求 -- 重置密码 {}", auth);
        val user = userRepo.findOneByLogin(auth.getLogin());
        if (!user.isPresent()) {
            throw new LoginNotFoundException();
        }
        user.get().setPassword(auth.getPassword());
        userRepo.save(user.get());
    }

    @PostMapping("/auth/captcha")
    public Captcha verifyCaptcha(@RequestBody final Captcha captcha) {
        val body = new HashMap<String, String>();
        body.put("captcha_code", captcha.getCode());
        body.put("captcha_token", captcha.getToken());
        val entity = new HttpEntity<>(body);
        try {
            val validateCaptcha = restTemplate.postForObject(captchaProperties.getVerificationUrl(), entity, Captcha.class);
            if (validateCaptcha == null)
                throw new InternalServerErrorException("返回对象为空，无法进行验证");
            return Captcha.builder()
                    .code(captcha.getCode())
                    .token(captcha.getToken())
                    .validatedMsg(validateCaptcha.getValidatedMsg()).build();
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
