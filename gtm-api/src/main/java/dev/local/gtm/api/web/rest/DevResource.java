package dev.local.gtm.api.web.rest;

import dev.local.gtm.api.config.Constants;
import dev.local.gtm.api.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于开发环境下，提供便于调试的接口，生产环境下不存在此接口
 */
@RequiredArgsConstructor
@Profile(Constants.SPRING_PROFILE_DEVELOPMENT)
@RestController
@RequestMapping("/dev")
public class DevResource {

  private final TokenProvider tokenProvider;

  @GetMapping("/admintoken")
  public String getAdminToken() {
    return tokenProvider.createDevAdminToken();
  }
}
