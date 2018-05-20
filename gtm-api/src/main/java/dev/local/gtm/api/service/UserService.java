package dev.local.gtm.api.service;

import dev.local.gtm.api.domain.User;
import dev.local.gtm.api.domain.search.UserSearch;
import dev.local.gtm.api.service.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(UserDTO userDTO);

    Optional<UserDTO> updateUser(UserDTO userDTO);

    void deleteUser(String login);

    Page<UserDTO> getAllManagedUsers(Pageable pageable);

    Optional<User> getUserWithAuthoritiesByLogin(String login);

    List<String> getAuthorities();

    Page<UserSearch> search(String query, Pageable pageable);

    void removeNotActivatedUsers();
}
