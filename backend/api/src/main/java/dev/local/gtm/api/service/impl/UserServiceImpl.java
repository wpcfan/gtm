package dev.local.gtm.api.service.impl;

import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.config.Constants;
import dev.local.gtm.api.domain.Authority;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.repository.AuthorityRepo;
import dev.local.gtm.api.repository.UserRepo;
import dev.local.gtm.api.service.UserService;
import dev.local.gtm.api.service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final AuthorityRepo authorityRepo;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

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
                    .map(authorityRepo::findById)
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
        userRepo.save(user);
        log.debug("用户: {}", user);
        return user;
    }

    @Override
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.of(userRepo.findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(user -> {
                    user.setName(userDTO.getName());
                    user.setAvatar(userDTO.getAvatar());
                    user.setEmail(userDTO.getEmail());
                    user.setMobile(userDTO.getMobile());
                    user.setActivated(userDTO.isActivated());
                    Set<Authority> managedAuthorities = user.getAuthorities();
                    managedAuthorities.clear();
                    userDTO.getAuthorities().stream()
                            .map(authorityRepo::findById)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .forEach(managedAuthorities::add);
                    userRepo.save(user);
                    log.debug("用户: {} 的数据已更新", user);
                    return user;
                })
                .map(UserDTO::new);
    }

    @Override
    public void deleteUser(String login) {
        userRepo.findOneByLogin(login).ifPresent(user -> {
            userRepo.delete(user);
            log.debug("已删除用户: {}", user);
        });
    }

    @Override
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepo.findAllByLoginNot(pageable, Constants.ANONYMOUS_USER).map(UserDTO::new);
    }

    @Override
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepo.findOneByLogin(login);
    }

    @Override
    public List<Authority> getAuthorities() {
        return authorityRepo.findAll();
    }
}
