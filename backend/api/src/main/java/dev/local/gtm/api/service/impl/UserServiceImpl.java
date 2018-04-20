package dev.local.gtm.api.service.impl;

import dev.local.gtm.api.domain.Authority;
import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.service.UserService;
import dev.local.gtm.api.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    @Override
    public User createUser(UserDTO userDTO) {
        return null;
    }

    @Override
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional.empty();
    }

    @Override
    public void deleteUser(String login) {

    }

    @Override
    public Page<UserDTO> getAllManagedUsers(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public List<Authority> getAuthorities() {
        return null;
    }
}
