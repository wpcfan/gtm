package dev.local.gtm.api.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.domain.Authority;
import dev.local.gtm.api.domain.Captcha;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.domain.search.UserSearch;
import dev.local.gtm.api.repository.mongo.AuthorityRepository;
import dev.local.gtm.api.repository.mongo.UserRepository;
import dev.local.gtm.api.repository.search.UserSearchRepository;
import dev.local.gtm.api.security.AuthoritiesConstants;
import dev.local.gtm.api.security.jwt.TokenProvider;
import dev.local.gtm.api.service.AuthService;
import dev.local.gtm.api.service.dto.UserDTO;
import dev.local.gtm.api.util.CredentialUtil;
import dev.local.gtm.api.web.exception.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    @Qualifier("leanCloudTemplate")
    private final RestTemplate leanCloudTemplate;

    private final UserRepository userRepository;

    private final AuthorityRepository authorityRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final AuthenticationManager authenticationManager;

    private final AppProperties appProperties;

    private final CacheManager cacheManager;

    private final UserSearchRepository userSearchRepository;

    @Override
    public void registerUser(UserDTO userDTO, String password) {
        if (usernameExisted(userDTO.getLogin())) {
            throw new LoginExistedException();
        }
        if (mobileExisted(userDTO.getMobile())) {
            throw new MobileExistedException();
        }
        if (emailExisted(userDTO.getEmail())) {
            throw new EmailExistedException();
        }
        val newUser = User.builder()
                .password(passwordEncoder.encode(password))
                .login(userDTO.getLogin())
                .mobile(userDTO.getMobile())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .avatar(userDTO.getAvatar())
                .activated(true)
                .authority(getOrCreateDefault())
                .build();
        log.debug("用户 {} 即将创建", newUser);
        userRepository.save(newUser);
        userSearchRepository.save(new UserSearch(newUser));
        this.clearUserCaches(newUser);
        log.debug("用户 {} 创建成功", newUser);
    }

    @Override
    public String login(String login, String password) {
        val authenticationToken = new UsernamePasswordAuthenticationToken(login, password);
        val authentication = this.authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return tokenProvider.createToken(authentication);
    }

    @Override
    public void requestSmsCode(String mobile, String validateToken) {
        sendSmsCode(mobile, validateToken);
    }

    @Override
    public String verifyMobile(String mobile, String code) {
        return userRepository.findOneByMobile(mobile)
                .map(user -> {
                    verifySmsCode(mobile, code);
                    user.setResetKey(CredentialUtil.generateResetKey());
                    userRepository.save(user);
                    return user.getResetKey();
                })
                .orElseThrow(MobileNotFoundException::new);
    }

    @Override
    public Captcha requestCaptcha() {
        val captcha = leanCloudTemplate.getForObject(appProperties.getCaptcha().getRequestUrl(), Captcha.class);
        if (captcha == null) {
            log.debug("由于某种原因，远程返回的是 200，但得到的对象为空");
            throw new InternalServerErrorException("请求 Captcha 返回对象为空");
        }
        return captcha;
    }

    @Override
    public String verifyCaptcha(String code, String token) {
        val body = new HashMap<String, String>();
        body.put("captcha_code", code);
        body.put("captcha_token", token);
        val entity = new HttpEntity<>(body);
        val validateToken = leanCloudTemplate.postForObject(appProperties.getCaptcha().getVerificationUrl(), entity, ValidateToken.class);
        if (validateToken == null) {
            log.debug("由于某种原因，远程返回的是 200，但得到的对象为空");
            throw new InternalServerErrorException("验证 Captcha 返回对象为空，无法进行验证");
        }
        return validateToken.getValidatedToken();
    }

    @Override
    public void resetPassword(String key, String mobile, String password) {
        userRepository.findOneByMobile(mobile)
                .map(user -> {
                    if (!user.getResetKey().equals(key)) {
                        log.debug("ResetKey 不匹配，客户端传递的 key 为：{}，期待值为 {} ", key, user.getResetKey());
                        throw new ResetKeyNotMatchException();
                    }
                    user.setPassword(passwordEncoder.encode(password));
                    user.setResetKey(null);
                    user.setResetDate(Instant.now());
                    return userRepository.save(user);
                })
                .orElseThrow(LoginNotFoundException::new);
    }

    @Override
    public boolean usernameExisted(String username) {
        return userRepository.countByLoginIgnoreCase(username) > 0;
    }

    @Override
    public boolean emailExisted(String email) {
        return userRepository.countByEmailIgnoreCase(email) > 0;
    }

    @Override
    public boolean mobileExisted(String mobile) {
        return userRepository.countByMobile(mobile) > 0;
    }

    private Authority getOrCreateDefault() {
        return authorityRepository.findOneByName(AuthoritiesConstants.USER)
            .orElse(authorityRepository.save(new Authority(AuthoritiesConstants.USER)));
    }

    private void verifySmsCode(final String mobile, final  String code) {
        val body = new HashMap<String, String>();
        body.put("mobilePhoneNumber", mobile);
        val entity = new HttpEntity<>(body);
        leanCloudTemplate.postForEntity(
                appProperties.getSmsCode().getVerificationUrl() + "/" + code,
                entity, Void.class);
    }

    private void sendSmsCode(String mobile, String validateToken) {
        val body = new HashMap<String, String>();
        body.put("mobilePhoneNumber", mobile);
        body.put("validate_token", validateToken);
        val entity = new HttpEntity<>(body);
        leanCloudTemplate.postForEntity(appProperties.getSmsCode().getRequestUrl(), entity, Void.class);
    }

    @Getter
    @Setter
    private static class ValidateToken {
        @JsonProperty("validate_token")
        private String validatedToken;
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(
                cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE))
                .evict(user.getLogin());
        Objects.requireNonNull(
                cacheManager.getCache(UserRepository.USERS_BY_MOBILE_CACHE))
                .evict(user.getMobile());
        Objects.requireNonNull(
                cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE))
                .evict(user.getEmail());
    }
}
