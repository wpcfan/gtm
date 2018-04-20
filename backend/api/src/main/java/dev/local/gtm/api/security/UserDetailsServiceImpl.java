package dev.local.gtm.api.security;

import dev.local.gtm.api.config.Constants;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户鉴权服务的具体实现
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Log4j2
@RequiredArgsConstructor
@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    /**
     * 通过数据库检验用户是否合法
     * @param login 用户名
     * @return 返回 Spring Security User
     */
    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("正在对用户名为 {} 的用户进行鉴权", login);

        if (new EmailValidator().isValid(login, null)) {
            val userByEmailFromDatabase = userRepo.findOneByEmailIgnoreCase(login);
            return userByEmailFromDatabase.map(user -> createSpringSecurityUser(login, user))
                    .orElseThrow(() -> new UsernameNotFoundException("系统中不存在 email 为 " + login + " 的用户"));
        }

        if (Pattern.matches(Constants.MOBILE_REGEX, login)) {
            val userByMobileFromDatabase = userRepo.findOneByMobile(login);
            return userByMobileFromDatabase.map(user -> createSpringSecurityUser(login, user))
                    .orElseThrow(() -> new UsernameNotFoundException("系统中不存在手机号为 " + login + " 的用户"));
        }

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        val userByLoginFromDatabase = userRepo.findOneByLogin(lowercaseLogin);
        return userByLoginFromDatabase.map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));

    }

    /**
     * 通过应用的用户领域对象创建 Spring Security 的用户
     *
     * 这里有两个 User ，为避免混淆，对于 Spring Security 的 User 采用 Full Qualified Name
     * @param lowercaseLogin 小写的用户登录名
     * @param user 领域对象
     * @return Spring Security User
     * @see org.springframework.security.core.userdetails.User
     */
    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("用户 " + lowercaseLogin + " 没有激活");
        }
        val grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getLogin(),
                user.getPassword(),
                grantedAuthorities);
    }
}
