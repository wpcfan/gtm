package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.config.SmsCaptchaProperties;
import dev.local.gtm.api.domain.Auth;
import dev.local.gtm.api.domain.Captcha;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.repository.UserRepo;
import dev.local.gtm.api.web.exception.InvalidPasswordException;
import dev.local.gtm.api.web.exception.LoginExistedException;
import dev.local.gtm.api.web.exception.LoginNotFoundException;
import dev.local.gtm.api.web.exception.MobileExistedException;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthResource {

    private final UserRepo userRepo;

    private final SmsCaptchaProperties captchaProperties;

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
            throw new MobileExistedException();
        }
        return ResponseEntity.ok(userRepo.save(user));
    }

//    @RequestMapping(value = "/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<User> resetPassword(@RequestBody User user) {
//
//    }

    private Captcha verifyCaptcha(final String code, final String token) {
        val restTemplate = new RestTemplate();
        val headers = new HttpHeaders();
        headers.add("X-LC-Id", captchaProperties.getAppId());
        headers.add("X-LC-Key", captchaProperties.getAppKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        val body = new HashMap<String, String>();
        body.put("captcha_code", code);
        body.put("captcha_token", token);
        val entity = new HttpEntity<>(body, headers);
        return restTemplate.postForObject(captchaProperties.getVerificationUrl(), entity, Captcha.class);
    }
}
