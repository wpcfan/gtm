package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.domain.search.UserSearch;
import dev.local.gtm.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserResource {
    private final UserService userService;

    @GetMapping("/_search/users/{query}")
    public Page<UserSearch> search(@PathVariable String query, final Pageable pageable) {
        return userService.search(query, pageable);
    }
}
