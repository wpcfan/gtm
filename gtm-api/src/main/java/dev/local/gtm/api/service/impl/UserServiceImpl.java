package dev.local.gtm.api.service.impl;

import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.config.Constants;
import dev.local.gtm.api.domain.Authority;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.domain.search.UserSearch;
import dev.local.gtm.api.repository.mongo.AuthorityRepository;
import dev.local.gtm.api.repository.mongo.UserRepository;
import dev.local.gtm.api.repository.search.UserSearchRepository;
import dev.local.gtm.api.service.UserService;
import dev.local.gtm.api.service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSearchRepository userSearchRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;
    private final CacheManager cacheManager;

    @Override
    public User createUser(UserDTO userDTO) {
        val user = User.builder()
            .login(userDTO.getLogin())
            .name(userDTO.getName())
            .email(userDTO.getEmail())
            .mobile(userDTO.getMobile())
            .avatar(userDTO.getAvatar())
            .build();
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO.getAuthorities().stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        String encryptedPassword = passwordEncoder.encode(appProperties.getUserDefaults().getInitialPassword());
        user.setPassword(encryptedPassword);
        user.setResetKey(null);
        user.setResetDate(Instant.now());
        user.setActivated(true);
        userRepository.save(user);
        userSearchRepository.save(new UserSearch(user));
        this.clearUserCaches(user);
        log.debug("用户: {}", user);
        return user;
    }

    @Override
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return userRepository.findById(userDTO.getId())
                .map(user -> {
                    user.setName(userDTO.getName());
                    user.setAvatar(userDTO.getAvatar());
                    user.setEmail(userDTO.getEmail());
                    user.setMobile(userDTO.getMobile());
                    user.setActivated(userDTO.isActivated());
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    userDTO.getAuthorities().stream()
                        .map(authorityRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(managedAuthorities::add);
                    userRepository.save(user);
                    userSearchRepository.save(new UserSearch(user));
                    this.clearUserCaches(user);
                    log.debug("用户: {} 的数据已更新", user);
                    return user;
                }).map(UserDTO::new);
    }

    @Override
    public void deleteUser(String login) {
        userRepository.findOneByLoginIgnoreCase(login)
        .ifPresent(user -> {
            userRepository.delete(user);
            userSearchRepository.delete(new UserSearch(user));
            this.clearUserCaches(user);
            log.debug("已删除用户: {}", user);
        });
    }

    @Override
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Override
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLoginIgnoreCase(login);
    }

    @Override
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream()
            .map(Authority::getName)
            .collect(Collectors.toList());
    }

    @Override
    public Page<UserSearch> search(String query, Pageable pageable) {
        return userSearchRepository.search(queryStringQuery(query), pageable);
    }

    /**
     * 超过 3 天的未激活用户会被删除
     * <p>
     * 这个任务每天凌晨 1 点或执行一次。
     */
    @Override
    @Async
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        List<User> users = userRepository
                .findAllByActivatedIsFalseAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS));
        for (User user : users) {
            log.debug("删除未激活用户 {}", user.getLogin());
            userRepository.delete(user);
            userSearchRepository.delete(new UserSearch(user));
            this.clearUserCaches(user);
        }
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evict(user.getLogin());
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_MOBILE_CACHE)).evict(user.getMobile());
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evict(user.getEmail());
    }
}
