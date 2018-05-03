package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.domain.search.UserSearch;
import dev.local.gtm.api.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/_search/users/{query}")
    public List<UserSearch> search(@PathVariable String query) {
        return userService.search(query);
    }
}
