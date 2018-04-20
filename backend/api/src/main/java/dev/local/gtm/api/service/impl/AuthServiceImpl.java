package dev.local.gtm.api.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.repository.AuthorityRepo;
import dev.local.gtm.api.repository.UserRepo;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Log4j2
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final AuthorityRepo authorityRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;
    private final AppProperties appProperties;

    @Override
    public void registerUser(UserDTO userDTO, String password) {
        if (userRepo.findOneByLogin(userDTO.getLogin()).isPresent()) {
            throw new LoginExistedException();
        }
        if (userRepo.findOneByMobile(userDTO.getMobile()).isPresent()) {
            throw new MobileExistedException();
        }
        if (userRepo.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
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
                .authorities(authorityRepo.findOneByName(AuthoritiesConstants.USER).orElseThrow(AuthorityNotFoundException::new))
                .build();
        userRepo.save(newUser);
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
    public String verifyMobile(String mobile, String code) {
        return userRepo.findOneByMobile(mobile)
                .map(user -> {
                    val status = verifySmsCode(mobile, code);
                    if (status.value() != 200) {
                        throw new MobileVerificationFailedException(status.getReasonPhrase());
                    }
                    user.setResetKey(CredentialUtil.generateResetKey());
                    userRepo.save(user);
                    return user.getResetKey();
                })
                .orElseThrow(MobileNotFoundException::new);
    }

    @Override
    public String verifyCaptcha(String code, String token) {
        val body = new HashMap<String, String>();
        body.put("captcha_code", code);
        body.put("captcha_token", token);
        val entity = new HttpEntity<>(body);
        try {
            val validateToken = restTemplate.postForObject(appProperties.getCaptcha().getVerificationUrl(), entity, ValidateToken.class);
            if (validateToken == null) {
                throw new InternalServerErrorException("返回对象为空，无法进行验证");
            }
            return validateToken.getValidatedToken();
        } catch (HttpStatusCodeException ex) {
            throw new CaptchaVerificationFailedException(ex.getStatusCode().getReasonPhrase());
        }
    }

    @Override
    public void resetPassword(String key, String mobile, String password) {
        userRepo.findOneByMobile(mobile)
                .map(user -> {
                    if (!user.getResetKey().equals(key)) {
                        throw new ResetKeyNotMatchException();
                    }
                    user.setPassword(passwordEncoder.encode(password));
                    user.setResetKey(null);
                    return userRepo.save(user);
                })
                .orElseThrow(LoginNotFoundException::new);
    }

    private HttpStatus verifySmsCode(final String mobile, final  String code) {
        val body = new HashMap<String, String>();
        body.put("mobilePhoneNumber", mobile);
        val entity = new HttpEntity<>(body);
        try {
            val response = restTemplate.postForEntity(
                    appProperties.getSmsCode().getVerificationUrl() + "/" + code,
                    entity, Void.class);
            return response.getStatusCode();
        } catch (HttpStatusCodeException ex) {
            return ex.getStatusCode();
        } catch (RestClientException ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    @Getter
    @Setter
    private static class ValidateToken {
        @JsonProperty("validate_token")
        private String validatedToken;
    }
}
